var ASMAPI = Java.type('net.neoforged.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var IntInsnNode = Java.type('org.objectweb.asm.tree.IntInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');

var TICK_THROTTLE_CLASSES = {
    "dev/simulated_team/simulated/content/blocks/gimbal_sensor/GimbalSensorBlockEntity": 4,
    "dev/simulated_team/simulated/content/blocks/velocity_sensor/VelocitySensorBlockEntity": 3,
    "dev/simulated_team/simulated/content/blocks/altitude_sensor/AltitudeSensorBlockEntity": 3,
    "dev/simulated_team/simulated/content/blocks/spring/SpringBlockEntity": 3,
    "dev/simulated_team/simulated/content/blocks/nav_table/NavTableBlockEntity": 4,
    "dev/simulated_team/simulated/content/blocks/lasers/optical_sensor/OpticalSensorBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/redstone_magnet/RedstoneMagnetBlockEntity": 3,
    "dev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorBlockEntity": 3,
    "dev/simulated_team/simulated/content/blocks/nameplate/NameplateBlockEntity": 4,
    "dev/simulated_team/simulated/content/blocks/rope/rope_winch/RopeWinchBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/swivel_bearing/SwivelBearingBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/physics_assembler/PhysicsAssemblerBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/redstone/AbstractLinkedReceiverBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/redstone/redstone_inductor/RedstoneInductorBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/throttle_lever/ThrottleLeverBlockEntity": 2,
    "dev/simulated_team/simulated/content/blocks/merging_glue/MergingGlueBlockEntity": 3
};

function initializeCoreMod() {
    var transformers = {};

    transformers["Rapier3DAndroidPatch"] = {
        "target": { "type": "CLASS", "name": "dev/ryanhcode/sable/physics/impl/rapier/Rapier3D" },
        "transformer": function(classNode) {
            for (var i = 0; i < classNode.methods.size(); i++) {
                var m = classNode.methods.get(i);
                if (m.name === "loadLibrary" && m.desc === "()V") {
                    patchLoadLibrary(m); break;
                }
            }
            return classNode;
        }
    };

    for (var className in TICK_THROTTLE_CLASSES) {
        (function(cn, interval) {
            transformers["TickThrottle_" + cn.replace(/\//g, '_')] = {
                "target": { "type": "CLASS", "name": cn },
                "transformer": function(classNode) {
                    for (var i = 0; i < classNode.methods.size(); i++) {
                        var m = classNode.methods.get(i);
                        if (m.name === "tick" && m.desc === "()V") {
                            patchTickThrottle(m, interval);
                            ASMAPI.log('INFO', '[SableAndroid] Patched tick(' + interval + ') on ' + cn.substring(cn.lastIndexOf('/') + 1));
                            break;
                        }
                    }
                    return classNode;
                }
            };
        })(className, TICK_THROTTLE_CLASSES[className]);
    }

    return transformers;
}

function patchLoadLibrary(method) {
    var insns = method.instructions;
    for (var i = 0; i < insns.size(); i++) {
        var insn = insns.get(i);
        if (insn.getOpcode() === Opcodes.INVOKESTATIC &&
            insn.owner === "java/lang/System" && insn.name === "load") {
            insns.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC,
                "dev/sableandroid/SableAndroidLoader", "load", "(Ljava/lang/String;)V", false));
            ASMAPI.log('INFO', '[SableAndroid] Patched Rapier3D.loadLibrary()');
            return;
        }
    }
}

function patchTickThrottle(method, interval) {
    var toInject = new InsnList();
    toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
    if (interval <= 5) {
        toInject.add(new InsnNode(Opcodes.ICONST_0 + interval));
    } else {
        toInject.add(new IntInsnNode(Opcodes.BIPUSH, interval));
    }
    toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,
        "dev/sableandroid/TickThrottleManager", "shouldTick", "(Ljava/lang/Object;I)Z", false));
    var skipReturn = new LabelNode();
    toInject.add(new JumpInsnNode(Opcodes.IFNE, skipReturn));
    toInject.add(new InsnNode(Opcodes.RETURN));
    toInject.add(skipReturn);
    method.instructions.insert(toInject);
}
