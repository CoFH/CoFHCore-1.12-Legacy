package cofh.core.world;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

@SuppressWarnings({ "resource", "unchecked" })
public class WorldGenMinableCell {

	private static ArrayList<List<Object>> oreList = new ArrayList<List<Object>>();
	public static int MPChunk_X;
	public static int MPChunk_Z;
	private int x_Chunk;
	private int z_Chunk;
	public Block MPBlockID;
	private int minableBlockMeta;
	public static int MPPrevX;
	public static int MPPrevZ;
	public static Block MPPrevID;
	public static int MPPrevMeta;
	private static boolean genBeforeCheck;
	public static int mineCount;
	public static int mineCountM;

	private static Random randomOut;
	private static Random rand;
	private static World worldObj;
	static File BODFile;
	static File configDirectory;
	static File BODbiomesFile;
	private static String whatWorld = "/OverWorld";
	String versionNum = "V(2.6)";

	private int mineGen = 1;
	private int subMineGen = 1;
	private int rarity = 2;
	private int veinSi = 2;
	private int veinAm = 2;
	private int height = 2;
	private int mineHeight = 2;
	private int diameter = 2;
	private int vDens = 2;
	private int hDens = 2;
	private Block minableBlockId;

	public WorldGenMinableCell(Block par1, int par2) {
		this(par1, par2, Blocks.stone);
	}

	public WorldGenMinableCell(Block par1, int par2, Block par3) {
		this.minableBlockId = par1;
	}

	public WorldGenMinableCell(Block id, int meta, int number, Block target) {
		this(id, number, target);
		minableBlockMeta = meta;
	}

	public boolean generateBeforeCheck() {
		genBeforeCheck = false;
		genBeforeCheck = oreList.contains(Arrays.asList(MPBlockID, minableBlockMeta));

		if (oreList.contains(Arrays.asList(MPBlockID, minableBlockMeta)) == false) {
			oreList.add(Arrays.asList(MPBlockID, minableBlockMeta));

		}
		return genBeforeCheck;
	}

	public void genOverrides(World world, Random random, int xCoord, int zCoord, Block resetTo) {

		BODFile = new File(configDirectory + "/" + versionNum + "BOD-Overrides.txt");
		File f = new File(configDirectory + "");
		Properties props = new Properties();
		if (f.exists() == false) {
			f.mkdirs();
		}
		if (BODFile.exists()) {
			boolean overrideExists = true;
			int count = 1;
			try {
				props.load(new FileInputStream(BODFile));
				String valPass = "0";
				while (overrideExists) {
					valPass = String.valueOf(count + ".add_new_ore");
					if (props.getProperty(valPass) != null) {
						valPass = props.getProperty(valPass);
						String[] strobj = valPass.split(":");
						minableBlockId = Block.getBlockFromName(strobj[0]);
						minableBlockMeta = Integer.valueOf(strobj[1]);
						generate(world, random, xCoord, zCoord, 1);
					} else {
						overrideExists = false;
					}
					count++;
				}
				minableBlockId = resetTo;
				minableBlockMeta = 0;
			} catch (IOException e) {

			}
		}
	}

	public boolean betterOreDistribution(int xChunk, int zChunk, Block mPBlockID2,
			int MPMinableBlockMeta) {
		rarity = 2;
		veinSi = 2;
		veinAm = 2;
		height = 2;
		mineHeight = 2;
		diameter = 2;
		vDens = 2;
		hDens = 2;
		String valPass = "1";

		Properties props = new Properties();
		try {

			BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine[" + mineGen + "."
					+ subMineGen + "].txt");
			if (BODFile.exists()) {
				props.load(new FileInputStream(BODFile));

				String valPass1 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta + "Rarity");
				String valPass2 = String
						.valueOf(mPBlockID2 + "." + MPMinableBlockMeta + "VeinSize");
				String valPass3 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "VeinAmount");
				String valPass4 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta + "Height");
				String valPass5 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "VerticalShift");
				String valPass6 = String
						.valueOf(mPBlockID2 + "." + MPMinableBlockMeta + "Diameter");
				String valPass7 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "VerticalDensity");
				String valPass8 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "HorizontalDensity");
				String valPass9 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "GenOreInBlockID");
				String valPass10 = String.valueOf(mPBlockID2 + "." + MPMinableBlockMeta
						+ "UseMarcoVeins");

				if (mineGen == 1 && subMineGen == 1) {
					if (props.getProperty(valPass1) == null || props.getProperty(valPass2) == null
							|| props.getProperty(valPass3) == null
							|| props.getProperty(valPass4) == null
							|| props.getProperty(valPass5) == null
							|| props.getProperty(valPass6) == null
							|| props.getProperty(valPass7) == null
							|| props.getProperty(valPass9) == null
							|| props.getProperty(valPass10) == null) {
						try {

							BufferedWriter out = new BufferedWriter(new FileWriter(BODFile, true));
							out.write("#NewBlock" + mPBlockID2 + "." + MPMinableBlockMeta + "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "Rarity=50" + "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "VeinSize=10"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "VeinAmount=70"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "Height=95" + "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "VerticalShift=0"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "Diameter=48"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "VerticalDensity=10"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta
									+ "HorizontalDensity=10" + "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "GenOreInBlockID=1"
									+ "\r\n");
							out.write(mPBlockID2 + "." + MPMinableBlockMeta + "UseMarcoVeins=false"
									+ "\r\n" + "\r\n");
							out.close();
						} catch (IOException h) {
						}
					}
				}
				if (props.getProperty(valPass1) != null) {
					valPass = props.getProperty(valPass1); // rarity
					rarity = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass2) != null) {
					valPass = props.getProperty(valPass2); // size
					veinSi = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass3) != null) {
					valPass = props.getProperty(valPass3); // amount
					veinAm = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass4) != null) {
					valPass = props.getProperty(valPass4); // height
					height = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass5) != null) {
					valPass = props.getProperty(valPass5); // mineHeight
					mineHeight = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass6) != null) {
					valPass = props.getProperty(valPass6); // diameter
					diameter = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass7) != null) {
					valPass = props.getProperty(valPass7); // vertical dense.
					vDens = Integer.valueOf(valPass);
				}

				if (props.getProperty(valPass8) != null) {
					valPass = props.getProperty(valPass8); // horiz. dense.
					hDens = Integer.valueOf(valPass);
				}
			}
		} catch (IOException e) {
			System.out.println("assigning variables had an exception!!!");
		}
		return true;
	}

	public boolean generate(World world, Random random, int i, int j, int k) {
		if (minableBlockId == Blocks.dirt) {
			whatWorld = "/OverWorld";
		}
		if (minableBlockId == Blocks.netherrack) {
			whatWorld = "/Nether";
		}
		configDirectory = new File("/BODprops/" + whatWorld);

		randomOut = random; // pad the seed so it's the same as vannila
		randomOut.nextFloat(); // |
		randomOut.nextInt(3); // |
		randomOut.nextInt(3); // |
		randomOut.nextDouble();// |
		if (minableBlockId == Blocks.dirt || minableBlockId == Blocks.netherrack) {
			MPChunk_X = ((i / 16) * 16);// set output chunk x // snap to grid
			MPChunk_Z = ((k / 16) * 16);// set output chunk z

			Random randomz = new Random(world.getSeed());
			long l = (randomz.nextLong() / 2L) * 2L + 1L; // |
			long l1 = (randomz.nextLong() / 2L) * 2L + 1L; // |
			randomz.setSeed(i * l + j * l1 ^ world.getSeed()); // |
			rand = randomz;

			worldObj = world;
			mineCount = 0;

			oreList.clear();
		}

		MPBlockID = minableBlockId;// set output block ID
		if (MPChunk_X != MPPrevX || MPChunk_Z != MPPrevZ || MPPrevID != MPBlockID
				|| minableBlockMeta != MPPrevMeta) {
			if (minableBlockId == Blocks.dirt || minableBlockId == Blocks.netherrack) {
				genOverrides(worldObj, rand, MPChunk_X, MPChunk_Z, minableBlockId);
			}
			if (generateBeforeCheck() == false) {
				MPPrevX = MPChunk_X;
				MPPrevZ = MPChunk_Z;
				x_Chunk = MPChunk_X;
				z_Chunk = MPChunk_Z;
				MPPrevID = MPBlockID;
				MPPrevMeta = minableBlockMeta;
				mineGen = 1;
				subMineGen = 1;

				BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine[1.1].txt");
				BODbiomesFile = new File(configDirectory + "/" + versionNum
						+ "BOD-biomes-Mine[1.1].txt");
				File f = new File(configDirectory + "");
				if (f.exists() == false) {
					f.mkdirs();
				}

				if (BODFile.exists() == false) {
					if (whatWorld == "/OverWorld") {
						writeBOD(BODFile);
					}
					if (whatWorld == "/Nether") {
						writeBODnether(BODFile);
					}
				}
				if (BODbiomesFile.exists() == false && whatWorld == "/OverWorld") {
					writeBODbiomes(BODbiomesFile);
				}

				while (BODFile.exists()) {
					betterOreDistribution(x_Chunk, z_Chunk, MPBlockID, minableBlockMeta);

					if (rarity > 0) {
						rarity = mPBiomeRarity(rarity, x_Chunk, z_Chunk, MPBlockID);
					}
					if (rarity == 1 || (rarity > 0 && rand.nextInt(rarity) == 0)) {
						while (BODFile.exists()) {
							betterOreDistribution(x_Chunk, z_Chunk, MPBlockID, minableBlockMeta);
							if (subMineGen == 1) {
								createMine(worldObj, rand, x_Chunk, z_Chunk);
							} else {
								createMineWithChance(worldObj, rand, x_Chunk, z_Chunk);
							}

							subMineGen++;
							BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine["
									+ mineGen + "." + subMineGen + "].txt");
						}
					}
					subMineGen = 1;
					mineGen++;
					BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine[" + mineGen
							+ "." + subMineGen + "].txt");
				}
			}
		}
		return true;
	}

	public int mPBiomeRarity(int biomeRar, int xChunkBio, int zChunkBio, Block mPBlockID2) {

		Properties props = new Properties();
		int biomeVals = rarity;
		String valPass = "1";
		String valPassB = "1";
		try {
			BODbiomesFile = new File(configDirectory + "/" + versionNum + "BOD-Biomes-Mine["
					+ mineGen + "." + subMineGen + "].txt");
			if (BODbiomesFile.exists()) {
				props.load(new FileInputStream(BODbiomesFile));

				valPass = String
						.valueOf("OreID["
								+ mPBlockID2
								+ "."
								+ minableBlockMeta
								+ "]-BiomeID["
								+ worldObj.getWorldChunkManager().getBiomeGenAt(xChunkBio,
										zChunkBio).biomeID + "]");

				if (props.getProperty(valPass) != null) {
					valPassB = props.getProperty(valPass);
					biomeVals = Integer.valueOf(valPassB);
				}
			}
		} catch (IOException j) {
		}
		if (valPass != null) {
			biomeRar = biomeVals;
		} else {
			biomeRar = rarity;
		}
		return biomeRar;

	}

	void createMineWithChance(World worldObj, Random rand, int x, int z) {
		rarity = mPBiomeRarity(rarity, x, z, MPBlockID);
		if (rarity == 1 || (rarity > 0 && rand.nextInt(rarity) == 0)) {
			createMine(worldObj, rand, x, z);
		}
	}

	public int mPCalculateDensity(int oreDistance, float oreDensity) {

		oreDensity = (oreDensity * 0.01f * (oreDistance >> 1)) + 1f;
		int i = (int)oreDensity;
		int rnd = oreDistance / i;
		int r = 0;
		for (; i > 0; --i) {
			r += rand.nextInt(rnd);
		}
		return r;
	}

	void createMine(World worldObj, Random rand, int x, int z) {
		for (int loopCount = 0; loopCount < veinAm; loopCount++) {
			int temp1 = mPCalculateDensity(diameter, hDens);
			int temp2 = mineHeight + mPCalculateDensity(height, vDens);
			int temp3 = mPCalculateDensity(diameter, hDens);
			int l5 = x + temp1;
			int i9 = temp2;
			int k13 = z + temp3;
			BODgenerateVein(worldObj, rand, l5, i9, k13, veinSi);
		}
	}

	public boolean BODgenerateVein(World world, Random rand, int parX, int parY, int parZ, int xyz) {
		// ==========================================mp mod
		int posX = parX;
		int posY = parY;
		int posZ = parZ;
		int posX2 = 0;
		int posY2 = 0;
		int posZ2 = 0;
		int directionX = 0;
		int directionY = 0;
		int directionZ = 0;
		int directionX2 = 0;
		int directionY2 = 0;
		int directionZ2 = 0;
		int directionChange = 0;
		int directionChange2 = 0;
		int blocksToUse = xyz;// input number of blocks per vein
		int blocksToUse2 = 0;
		for (int blocksMade = 0; blocksMade <= blocksToUse;) // make veins
		{
			blocksToUse2 = 1 + (blocksToUse / 30);
			directionChange = rand.nextInt(6);
			directionX = rand.nextInt(2);
			directionY = rand.nextInt(2);
			directionZ = rand.nextInt(2);

			for (int blocksMade1 = 0; blocksMade1 <= blocksToUse2;) // make
																	// branch
			{
				if (directionX == 0 && directionChange != 1) {
					posX = posX + rand.nextInt(2);
				}
				if (directionX == 1 && directionChange != 1) {
					posX = posX - rand.nextInt(2);
				}
				if (directionY == 0 && directionChange != 2) {
					posY = posY + rand.nextInt(2);
				}
				if (directionY == 1 && directionChange != 2) {
					posY = posY - rand.nextInt(2);
				}
				if (directionZ == 0 && directionChange != 3) {
					posZ = posZ + rand.nextInt(2);
				}
				if (directionZ == 1 && directionChange != 3) {
					posZ = posZ - rand.nextInt(2);
				}
				if (rand.nextInt(4) == 0) {
					posX2 = posX2 + rand.nextInt(2);
					posY2 = posY2 + rand.nextInt(2);
					posZ2 = posZ2 + rand.nextInt(2);
					posX2 = posX2 - rand.nextInt(2);
					posY2 = posY2 - rand.nextInt(2);
					posZ2 = posZ2 - rand.nextInt(2);
				}
				if (rand.nextInt(3) == 0) // make sub-branch
				{
					posX2 = posX;
					posY2 = posY;
					posZ2 = posZ;

					directionX2 = rand.nextInt(2);
					directionY2 = rand.nextInt(2);
					directionZ2 = rand.nextInt(2);
					directionChange2 = rand.nextInt(6);
					if (directionX2 == 0 && directionChange2 != 0) {
						posX2 = posX2 + rand.nextInt(2);
					}
					if (directionY2 == 0 && directionChange2 != 1) {
						posY2 = posY2 + rand.nextInt(2);
					}
					if (directionZ2 == 0 && directionChange2 != 2) {
						posZ2 = posZ2 + rand.nextInt(2);
					}
					if (directionX2 == 1 && directionChange2 != 0) {
						posX2 = posX2 - rand.nextInt(2);
					}
					if (directionY2 == 1 && directionChange2 != 1) {
						posY2 = posY2 - rand.nextInt(2);
					}
					if (directionZ2 == 1 && directionChange2 != 2) {
						posZ2 = posZ2 - rand.nextInt(2);
					}

					for (int blocksMade2 = 0; blocksMade2 <= (1 + (blocksToUse2 / 5));) {

						if (directionX2 == 0 && directionChange2 != 0) {
							posX2 = posX2 + rand.nextInt(2);
						}
						if (directionY2 == 0 && directionChange2 != 1) {
							posY2 = posY2 + rand.nextInt(2);
						}
						if (directionZ2 == 0 && directionChange2 != 2) {
							posZ2 = posZ2 + rand.nextInt(2);
						}
						if (directionX2 == 1 && directionChange2 != 0) {
							posX2 = posX2 - rand.nextInt(2);
						}
						if (directionY2 == 1 && directionChange2 != 1) {
							posY2 = posY2 - rand.nextInt(2);
						}
						if (directionZ2 == 1 && directionChange2 != 2) {
							posZ2 = posZ2 - rand.nextInt(2);
						}
						if (world.getBlock(posX, posY, posZ) == Blocks.stone
								|| world.getBlock(posX, posY, posZ) == Blocks.netherrack) {
							world.setBlock(posX, posY, posZ, MPBlockID, minableBlockMeta, 2);
						}
						blocksMade++;
						blocksMade1++;
						blocksMade2++;
					}
				}

				if (world.getBlock(posX, posY, posZ) == Blocks.stone
						|| world.getBlock(posX, posY, posZ) == Blocks.netherrack) {
					world.setBlock(posX, posY, posZ, MPBlockID, minableBlockMeta, 2);
				}

				blocksMade++;
				blocksMade1++;

			}

			parX = parX + (rand.nextInt(3) - 1);
			parY = parY + (rand.nextInt(3) - 1);
			parZ = parZ + (rand.nextInt(3) - 1);
			posX = parX;
			posY = parY;
			posZ = parZ;

		}

		return true;
	}

	public boolean writeBOD(File writeTo) {
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("#see forum for more instructions: http://www.minecraftforum.net/topic/330485-10-marcopolos-mods/"
					+ "\r\n");
			out.write("#format X.Ysetting=Z" + "\r\n");
			out.write("# X = block ID" + "\r\n");
			out.write("# Y = meta data" + "\r\n");
			out.write("# Z = value for setting" + "\r\n");

			out.write("#Dirt" + "\r\n"); // dirt
			out.write("3.0Rarity=50" + "\r\n");
			out.write("3.0VeinSize=30" + "\r\n");
			out.write("3.0VeinAmount=17" + "\r\n");
			out.write("3.0Height=128" + "\r\n");
			out.write("3.0VerticalShift=0" + "\r\n");
			out.write("3.0Diameter=32" + "\r\n");
			out.write("3.0VerticalDensity=1" + "\r\n");
			out.write("3.0HorizontalDensity=1" + "\r\n");
			out.write("3.0GenOreInBlockID=1" + "\r\n");
			out.write("3.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Gravel" + "\r\n"); // gravel
			out.write("13.0Rarity=50" + "\r\n");
			out.write("13.0VeinSize=30" + "\r\n");
			out.write("13.0VeinAmount=17" + "\r\n");
			out.write("13.0Height=128" + "\r\n");
			out.write("13.0VerticalShift=0" + "\r\n");
			out.write("13.0Diameter=32" + "\r\n");
			out.write("13.0VerticalDensity=1" + "\r\n");
			out.write("13.0HorizontalDensity=1" + "\r\n");
			out.write("13.0GenOreInBlockID=1" + "\r\n");
			out.write("13.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Gold" + "\r\n"); // gold
			out.write("14.0Rarity=140" + "\r\n");
			out.write("14.0VeinSize=8" + "\r\n");
			out.write("14.0VeinAmount=135" + "\r\n");
			out.write("14.0Height=80" + "\r\n");
			out.write("14.0VerticalShift=0" + "\r\n");
			out.write("14.0Diameter=60" + "\r\n");
			out.write("14.0VerticalDensity=20" + "\r\n");
			out.write("14.0HorizontalDensity=10" + "\r\n");
			out.write("14.0GenOreInBlockID=1" + "\r\n");
			out.write("14.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Iron" + "\r\n"); // Iron
			out.write("15.0Rarity=75" + "\r\n");
			out.write("15.0VeinSize=8" + "\r\n");
			out.write("15.0VeinAmount=220" + "\r\n");
			out.write("15.0Height=80" + "\r\n");
			out.write("15.0VerticalShift=0" + "\r\n");
			out.write("15.0Diameter=65" + "\r\n");
			out.write("15.0VerticalDensity=15" + "\r\n");
			out.write("15.0HorizontalDensity=15" + "\r\n");
			out.write("15.0GenOreInBlockID=1" + "\r\n");
			out.write("15.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Coal" + "\r\n"); // coal
			out.write("16.0Rarity=80" + "\r\n");
			out.write("16.0VeinSize=7" + "\r\n");
			out.write("16.0VeinAmount=330" + "\r\n");
			out.write("16.0Height=6" + "\r\n");
			out.write("16.0VerticalShift=45" + "\r\n");
			out.write("16.0Diameter=70" + "\r\n");
			out.write("16.0VerticalDensity=85" + "\r\n");
			out.write("16.0HorizontalDensity=10" + "\r\n");
			out.write("16.0GenOreInBlockID=1" + "\r\n");
			out.write("16.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Lapis" + "\r\n"); // lapis
			out.write("21.0Rarity=225" + "\r\n");
			out.write("21.0VeinSize=8" + "\r\n");
			out.write("21.0VeinAmount=200" + "\r\n");
			out.write("21.0Height=50" + "\r\n");
			out.write("21.0VerticalShift=0" + "\r\n");
			out.write("21.0Diameter=70" + "\r\n");
			out.write("21.0VerticalDensity=20" + "\r\n");
			out.write("21.0HorizontalDensity=10" + "\r\n");
			out.write("21.0GenOreInBlockID=1" + "\r\n");
			out.write("21.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Diamond" + "\r\n"); // daimond
			out.write("56.0Rarity=160" + "\r\n");
			out.write("56.0VeinSize=8" + "\r\n");
			out.write("56.0VeinAmount=220" + "\r\n");
			out.write("56.0Height=70" + "\r\n");
			out.write("56.0VerticalShift=0" + "\r\n");
			out.write("56.0Diameter=70" + "\r\n");
			out.write("56.0VerticalDensity=20" + "\r\n");
			out.write("56.0HorizontalDensity=10" + "\r\n");
			out.write("56.0GenOreInBlockID=1" + "\r\n");
			out.write("56.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Redstone" + "\r\n"); // redstone
			out.write("73.0Rarity=110" + "\r\n");
			out.write("73.0VeinSize=12" + "\r\n");
			out.write("73.0VeinAmount=160" + "\r\n");
			out.write("73.0Height=12" + "\r\n");
			out.write("73.0VerticalShift=0" + "\r\n");
			out.write("73.0Diameter=160" + "\r\n");
			out.write("73.0VerticalDensity=20" + "\r\n");
			out.write("73.0HorizontalDensity=5" + "\r\n");
			out.write("73.0GenOreInBlockID=1" + "\r\n");
			out.write("73.0UseMarcoVeins=false" + "\r\n" + "\r\n");

			out.write("#Emerald" + "\r\n"); // emerald
			out.write("129.0Rarity=110" + "\r\n");
			out.write("129.0VeinSize=3" + "\r\n");
			out.write("129.0VeinAmount=600" + "\r\n");
			out.write("129.0Height=60" + "\r\n");
			out.write("129.0VerticalShift=0" + "\r\n");
			out.write("129.0Diameter=60" + "\r\n");
			out.write("129.0VerticalDensity=20" + "\r\n");
			out.write("129.0HorizontalDensity=5" + "\r\n");
			out.write("129.0GenOreInBlockID=1" + "\r\n");
			out.write("129.0UseMarcoVeins=false" + "\r\n" + "\r\n");
			out.close();
		} catch (IOException f) {
			System.out.println("could not write BODprops"); // / for debugging
		}
		return true;
	}

	public boolean writeBODnether(File writeTo) {
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("#see forum for more instructions: http://www.minecraftforum.net/topic/330485-10-marcopolos-mods/"
					+ "\r\n");
			out.write("#format X.Ysetting=Z" + "\r\n");
			out.write("# X = block ID" + "\r\n");
			out.write("# Y = meta data" + "\r\n");
			out.write("# Z = value for setting" + "\r\n");

			out.write("#Dirt" + "\r\n"); // dirt
			out.write("153.0Rarity=50" + "\r\n");
			out.write("153.0VeinSize=100" + "\r\n");
			out.write("153.0VeinAmount=20" + "\r\n");
			out.write("153.0Height=128" + "\r\n");
			out.write("153.0VerticalShift=0" + "\r\n");
			out.write("153.0Diameter=32" + "\r\n");
			out.write("153.0VerticalDensity=15" + "\r\n");
			out.write("153.0HorizontalDensity=15" + "\r\n");
			out.write("153.0GenOreInBlockID=87" + "\r\n");
			out.write("153.0UseMarcoVeins=true" + "\r\n" + "\r\n");
			out.close();
		} catch (IOException f) {
			System.out.println("could not write BODprops"); // / for debugging
		}
		return true;
	}

	public boolean writeBODbiomes(File writeTo) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("# 3.0=dirt, 13.0=gravel, 14.0=gold, 15.0=iron, 16.0=coal, 21.0=lapis, 56.0=diamond, 73.0=redstone, 129.0=emerald"
					+ "\r\n");
			out.write("\r\n");
			out.write("# ------------------------------------------------------------" + "\r\n");
			out.write("# format is OreID[X.Xo]-BiomeID[Y]=Z" + "\r\n");
			out.write("# X = Ore ID" + "\r\n");
			out.write("# Xo = meta data for Ore ID" + "\r\n");
			out.write("# Y = Biome ID" + "\r\n");
			out.write("# Z = the rarity you want for that ore in that biome" + "\r\n");
			out.write("# ------------------------------------------------------------" + "\r\n");
			out.write("\r\n");
			out.write("#-----------Ocean 0" + "\r\n");
			out.write("OreID[21.0]-BiomeID[0]=75" + "\r\n");
			out.write("\r\n");

			out.write("#-----------Plains 1" + "\r\n");
			out.write("OreID[3.0]-BiomeID[1]=15" + "\r\n");
			out.write("OreID[13.0]-BiomeID[1]=15" + "\r\n");
			out.write("\r\n");

			out.write("#-----------Desert 2" + "\r\n");
			out.write("OreID[15.0]-BiomeID[2]=37" + "\r\n");
			out.write("\r\n");

			out.write("#-----------extremeHills 3" + "\r\n");
			out.write("OreID[14.0]-BiomeID[3]=70" + "\r\n");
			out.write("OreID[129.0]-BiomeID[3]=50" + "\r\n");
			out.write("\r\n");

			out.write("#-----------Forest 4" + "\r\n");
			out.write("OreID[15.0]-BiomeID[4]=37" + "\r\n");
			out.write("\r\n");

			out.write("#-----------Taiga 5" + "\r\n");
			out.write("OreID[16.0]-BiomeID[5]=42" + "\r\n");
			out.write("\r\n");

			out.write("#-----------Swampland 6" + "\r\n");
			out.write("OreID[73.0]-BiomeID[6]=55" + "\r\n");
			out.write("\r\n");

			out.write("#-----------River 7" + "\r\n");
			out.write("OreID[14.0]-BiomeID[7]=70" + "\r\n");
			out.write("\r\n");

			out.write("#-----------FrozenOcean 10" + "\r\n");
			out.write("\r\n");

			out.write("#-----------FrozenRiver 11" + "\r\n");
			out.write("\r\n");

			out.write("#-----------IcePlains 12" + "\r\n");
			out.write("\r\n");

			out.write("#-----------IceMountain 13" + "\r\n");
			out.write("\r\n");

			out.write("#-----------MushroomIsland 14" + "\r\n");
			out.write("OreID[13.0]-BiomeID[14]=40" + "\r\n");
			out.write("OreID[14.0]-BiomeID[14]=50" + "\r\n");
			out.write("OreID[15.0]-BiomeID[14]=50" + "\r\n");
			out.write("OreID[16.0]-BiomeID[14]=50" + "\r\n");
			out.write("OreID[21.0]-BiomeID[14]=50" + "\r\n");
			out.write("OreID[56.0]-BiomeID[14]=50" + "\r\n");
			out.write("OreID[73.0]-BiomeID[14]=50" + "\r\n");
			out.write("\r\n");
			out.write("#-----------mushroomshore 15" + "\r\n");
			out.write("\r\n");
			out.write("#-----------Beach 16" + "\r\n");
			out.write("\r\n");
			out.write("#-----------DesertHills 17" + "\r\n");
			out.write("\r\n");
			out.write("#-----------ForestHills 18" + "\r\n");
			out.write("\r\n");
			out.write("#-----------taigaHills 19" + "\r\n");
			out.write("\r\n");
			out.write("#-----------ExtremeHillsEdge 20" + "\r\n");
			out.write("\r\n");
			out.write("#-----------Jungle 21" + "\r\n");
			out.write("\r\n");
			out.write("#-----------JungleHills 22" + "\r\n");
			out.write("\r\n");

			out.close();
		} catch (IOException i) {
			System.out.println("could not write BODbiomes!!!"); // / for
																// debugging
		}
		return true;
	}// */
}