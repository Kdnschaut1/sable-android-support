package org.kdnschaut.sablebridge.mixin;

import com.google.gson.Gson;
import net.neoforged.fml.loading.FMLPaths;
import org.kdnschaut.sablebridge.SableBridgeLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import sun.misc.Unsafe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class SodiumCheckBypassMixin implements IMixinConfigPlugin {
    private static final Gson GSON = new Gson();

    @Override
    public void onLoad(String mixinPackage) {
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            SableBridgeLogger.logBridge("Initializing inline Sodium mobile compatibility bypass...");

            tryFindClass("net/caffeinemc/mods/sodium/client/compatibility/checks/PreLaunchChecks").ifPresentOrElse((bytes) -> {
                try {
                    defineClass(loader, "net.caffeinemc.mods.sodium.client.compatibility.checks.PreLaunchChecks",
                            transformPreLaunch(bytes), SodiumCheckBypassMixin.class.getProtectionDomain());
                    SableBridgeLogger.logBridge("Successfully transformed and loaded PreLaunchChecks");
                } catch (Throwable e) {
                    SableBridgeLogger.logBridgeError("Was unable to define class PreLaunchChecks: " + e.getMessage());
                }
            }, () -> SableBridgeLogger.logBridge("Class PreLaunchChecks not found (Sodium might not be installed)."));

            tryFindClass("net/caffeinemc/mods/sodium/client/compatibility/checks/PostLaunchChecks").ifPresentOrElse((bytes) -> {
                try {
                    defineClass(loader, "net.caffeinemc.mods.sodium.client.compatibility.checks.PostLaunchChecks",
                            transformPostLaunch(bytes), SodiumCheckBypassMixin.class.getProtectionDomain());
                    SableBridgeLogger.logBridge("Successfully transformed and loaded PostLaunchChecks");
                } catch (Throwable e) {
                    SableBridgeLogger.logBridgeError("Was unable to define class PostLaunchChecks: " + e.getMessage());
                }
            }, () -> {});

        } catch (Throwable ignored) {
        }
    }

    private byte[] transformPreLaunch(byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 3);
        PreLaunchInjector injector = new PreLaunchInjector(writer, "checkLwjglRuntimeVersion", "()V");
        reader.accept(injector, 0);
        return writer.toByteArray();
    }

    private byte[] transformPostLaunch(byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, 3);
        PostLaunchInjector injector = new PostLaunchInjector(writer, "isUsingPojavLauncher", "()Z");
        reader.accept(injector, 0);
        return writer.toByteArray();
    }

    private static class PreLaunchInjector extends ClassVisitor {
        private final String methodName;
        private final String methodDesc;

        public PreLaunchInjector(ClassVisitor cv, String methodName, String methodDesc) {
            super(589824, cv);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return name.equals(this.methodName) && desc.equals(this.methodDesc) ? new MethodVisitor(589824, mv) {
                public void visitCode() {
                    super.visitCode();
                    super.visitInsn(177);
                }
            } : mv;
        }
    }

    private static class PostLaunchInjector extends ClassVisitor {
        private final String methodName;
        private final String methodDesc;

        public PostLaunchInjector(ClassVisitor cv, String methodName, String methodDesc) {
            super(589824, cv);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return name.equals(this.methodName) && desc.equals(this.methodDesc) ? new MethodVisitor(589824, mv) {
                public void visitCode() {
                    super.visitCode();
                    super.visitInsn(3);
                    super.visitInsn(172);
                }
            } : mv;
        }
    }

    private Optional<byte[]> tryFindClass(String className) {
        File modsDir = FMLPaths.MODSDIR.get().toFile();
        File[] foundMods = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        List<File> files = new ArrayList<>(foundMods != null ? Arrays.asList(foundMods) : Collections.emptyList());
        files.addAll(listClasspathEntries());

        for (File mod : files) {
            try (JarFile jar = new JarFile(mod)) {
                Optional<byte[]> result = scanJar(Files.readAllBytes(mod.toPath()), className);
                if (result.isPresent()) return result;
            } catch (IOException ignored) {}
        }
        return Optional.empty();
    }

    private Optional<byte[]> scanJar(byte[] jarBytes, String className) {
        try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().equals(className + ".class")) {
                    return Optional.of(jis.readAllBytes());
                }
                jis.closeEntry();
            }
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    private Class<?> defineClass(ClassLoader loader, String className, byte[] bytecode, ProtectionDomain protectionDomain) {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);

            int i;
            for (i = 0; unsafe.getBoolean(f, (long) i) == unsafe.getBoolean(f, (long) i); ++i) {}

            Field f2 = Unsafe.class.getDeclaredField("theInternalUnsafe");
            unsafe.putBoolean(f2, (long) i, true);
            Object internalUnsafe = f2.get(null);
            Method defineClass = internalUnsafe.getClass().getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ClassLoader.class, ProtectionDomain.class);
            unsafe.putBoolean(defineClass, (long) i, true);
            return (Class<?>) defineClass.invoke(internalUnsafe, className, bytecode, 0, bytecode.length, loader, protectionDomain);
        } catch (Exception e) {
            SableBridgeLogger.logBridgeError("Unable to dynamically define class " + className + " via Unsafe: " + e.getMessage());
            return null;
        }
    }

    private List<File> listClasspathEntries() {
        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        List<File> result = new ArrayList<>(classPathEntries.length);
        for (String entry : classPathEntries) {
            result.add(new File(entry));
        }
        return result;
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return true; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return List.of(); }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}