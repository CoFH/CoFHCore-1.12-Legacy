list i_preGenWorld
ALOAD 2
ILOAD 0
ILOAD 1
INVOKESTATIC cofh/asm/hooks/ASMHooks.preGenerateWorld(Lnet/minecraft/world/World;II)V

list i_postGenWorld
ALOAD 2
ILOAD 0
ILOAD 1
INVOKESTATIC cofh/asm/hooks/ASMHooks.postGenerateWorld(Lnet/minecraft/world/World;II)V

list n_fogColour
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_78535_ad : F
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_78539_ae : F
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_78535_ad : F

list i_fogColour
ALOAD 12
INVOKEINTERFACE net/minecraft/block/state/IBlockState.func_177230_c ()Lnet/minecraft/block/Block;
INSTANCEOF cofh/core/block/IFogOverlay
IFEQ LEND
ALOAD 12
INVOKEINTERFACE net/minecraft/block/state/IBlockState.func_177230_c ()Lnet/minecraft/block/Block;
ALOAD 12
ALOAD 3
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_175080_Q : F
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_175082_R : F
ALOAD 0
GETFIELD net/minecraft/client/renderer/EntityRenderer.field_175081_S : F
INVOKEINTERFACE cofh/core/block/IFogOverlay.getFog (Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/entity/Entity;FFF)Lnet/minecraft/util/math/Vec3d;
ASTORE 13
ALOAD 0
ALOAD 13
GETFIELD net/minecraft/util/math/Vec3d.field_72450_a : D
D2F
PUTFIELD net/minecraft/client/renderer/EntityRenderer.field_175080_Q : F
ALOAD 0
ALOAD 13
GETFIELD net/minecraft/util/math/Vec3d.field_72448_b : D
D2F
PUTFIELD net/minecraft/client/renderer/EntityRenderer.field_175082_R : F
ALOAD 0
ALOAD 13
GETFIELD net/minecraft/util/math/Vec3d.field_72449_c : D
D2F
PUTFIELD net/minecraft/client/renderer/EntityRenderer.field_175081_S : F
GOTO LEND
LEND
