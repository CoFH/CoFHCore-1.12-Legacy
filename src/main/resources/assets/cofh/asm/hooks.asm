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
