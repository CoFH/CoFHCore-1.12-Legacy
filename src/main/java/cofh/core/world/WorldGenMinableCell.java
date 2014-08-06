package cofh.core.world;

//Referenced classes of package net.minecraft.src:
//                    WorldGenerator, MathHelper, World, Block


public class WorldGenMinableCell {
/*
	//==========================================mp mod
	//private static int[] aOreCheck = new int[256];// setup array to store oreIDs for this chunk // has to be static to survive instance calls                
	//private static int[] metaOreCheck = new int[16];// this is used to check the metaIDs of a given ore ID
	private static ArrayList oreList = new ArrayList();
	public static int MPChunk_X;
	public static int MPChunk_Z;
	private int x_Chunk;
	private int z_Chunk;
	public int MPBlockID;
	private int minableBlockMeta;
	public static int MPPrevX;
	public static int MPPrevZ;
	public static int MPPrevID;
	public static int MPPrevMeta;
	//public static int MPPrevID3;
	//public static int MPPrevID4;
	private static boolean genBeforeCheck;
	public static int mineCount;
	public static int mineCountM;

	private static Random randomOut;
	private static Random rand;
	private static World worldObj;
	private File BODFileOres;
	static File BODFile;
	static File configDirectory;
	static File BODbiomesFile;
	private static String whatWorld = "/OverWorld";
	String versionNum = "V(2.6)";
	private static WorldChunkManager worldChunkManager;
	private static WorldChunkManagerHell worldChunkManagerHell;

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
	private int genInBlock = 1;
	private boolean useMarcoVeins = false;

	//==========================================mp mod
	private Block minableBlockId;
	private int numberOfBlocks;
	private Block field_94523_c;

	public WorldGenMinableCell(Block par1, int par2)
	{
		this(par1, par2, Blocks.stone);
	}

	public WorldGenMinableCell(Block par1, int par2, Block par3)
	{
		this.minableBlockId = par1;
		this.numberOfBlocks = par2;
		this.field_94523_c = par3;
	}

	public WorldGenMinableCell(Block id, int meta, int number, int target)
	{
		this(id, number, target);
		minableBlockMeta = meta;
	}

	public boolean generateBeforeCheck() // takes a set of current global variables and checks to see if this ore has spawned before in this chunk
	{
		genBeforeCheck = false;
		genBeforeCheck = oreList.contains(Arrays.asList(MPBlockID, minableBlockMeta));

		if(oreList.contains(Arrays.asList(MPBlockID, minableBlockMeta)) == false)
		{
			oreList.add(Arrays.asList(MPBlockID, minableBlockMeta));

		}
		return genBeforeCheck;
	}
	public void genOverrides(World world, Random random, int xCoord, int zCoord, int resetTo)
	{

		BODFile = new File(configDirectory + "/" + versionNum + "BOD-Overrides.txt");
		File f = new File(configDirectory + ""); 
		Properties props = new Properties();
		if(f.exists()==false)// make the folder if it doesnt exist
		{
			f.mkdirs();
		}
		if(BODFile.exists()==false) // make the file if it doesnt exist
		{
			if(whatWorld=="/OverWorld")
			{
				writeBODoverrides(BODFile);
			}
			if(whatWorld=="/Nether")
			{
				writeBODnetherOverrides(BODFile);
			}
			//System.out.println("write BODprops"); /// for debugging
		}
		if(BODFile.exists())
		{
			boolean overrideExists = true;
			int count = 1;
			try
			{
				props.load(new FileInputStream(BODFile));
				String valPass = "0";
				while(overrideExists) // loop until we dont have anymore overrids listed
				{
					valPass = String.valueOf(count + ".add_new_ore"); // find the override string
					if (props.getProperty(valPass)!= null) // make sure it exists
					{
						valPass = props.getProperty(valPass); // get the text data off the end
						String[] strobj = valPass.split(":");
						minableBlockId = Integer.valueOf(strobj[0]);
						minableBlockMeta = Integer.valueOf(strobj[1]);
						//System.out.println(minableBlockId +" " + minableBlockMeta + "outputs"); for debugging
						generate(world, random, xCoord, zCoord, 1);
					}
					else
					{
						overrideExists = false;
					}
					count++;
				}
				minableBlockId = resetTo; // set it back to dirt before leaving
				minableBlockMeta = 0;
			}
			catch(IOException e)
			{

			}
		}
	}
	void createMine( World worldObj, Random rand, int x, int z)
	{
		for(int loopCount = 0; loopCount < veinAm; loopCount++)
		{
			int temp1 = mPCalculateDensity(diameter, hDens);
			int temp2 = mineHeight + mPCalculateDensity(height, vDens);
			int temp3 = mPCalculateDensity(diameter, hDens);
			int l5 = x + temp1;
			int i9 = temp2;
			int k13 = z + temp3;
			if(useMarcoVeins == false)
			{
				BODgenerate(worldObj, rand, l5, i9, k13, veinSi); // generate based on values
			}
			else
			{
				BODgenerateVein(worldObj, rand, l5, i9, k13, veinSi);
			}
		}
	}
	void createMineWithChance(World worldObj, Random rand, int x, int z)
	{
		rarity = mPBiomeRarity(rarity, x, z, MPBlockID); // retrieve rarity for this ore in this biome
		if (rarity == 1 || (rarity > 0 && rand.nextInt(rarity) == 0)) // use values
		{
			createMine(worldObj, rand, x, z);
		}
	}
	public boolean generate(World world, Random random, int i, int j, int k)//obsorb default system
	{
		if(minableBlockId == 3) {whatWorld = "/OverWorld";}
		if(minableBlockId == 153) {whatWorld = "/Nether";}
		configDirectory = new File(Minecraft.getMinecraftDir() + "/BODprops/" + whatWorld);// used for switching the code to singleplayer
		//configDirectory = new File(new File(".").getAbsolutePath() + "/BODprops/" + whatWorld); // used for switching the code to multiplayer   

		//System.out.println(" 1: call "+minableBlockId+":"+minableBlockMeta); // for debugging
		randomOut = random;     // pad the seed so it's the same as vannila
		randomOut.nextFloat(); //   |
		randomOut.nextInt(3);  //   |                          
		randomOut.nextInt(3);  //   |
		randomOut.nextDouble();//   |
		if (minableBlockId==3 || minableBlockId==153)// this makes sure everything is on a once per ore gen basis, because dirt generates first it sets everything up
		{      
			//System.out.println(" 1.2: found dirt, setting up"); /// for debugging
			MPChunk_X =((i / 16) * 16);// set output chunk x // snap to grid
			MPChunk_Z =((k / 16) * 16);// set output chunk z    

			Random randomz = new Random(world.getSeed()); // setup a random for BOD
			long l = (randomz.nextLong() / 2L) * 2L + 1L;                       // |
			long l1 = (randomz.nextLong() / 2L) * 2L + 1L;                      // |
			randomz.setSeed((long)i * l + (long)j * l1 ^ world.getSeed());      // |
			rand = randomz;

			worldObj = world; // set world
			mineCount = 0; // this is a new chunk, so list gets set to the beginning

			oreList.clear(); // clear the list of ores, this is a new chunk

		}	

		MPBlockID = minableBlockId;// set output block ID
		if(MPChunk_X != MPPrevX || MPChunk_Z != MPPrevZ || MPPrevID != MPBlockID || minableBlockMeta != MPPrevMeta)// if it is a new x or z chunk, then generate // blockID stops dirt
		{
			//System.out.println(" 2: allowed ore chunk prev test"); /// for debugging
			if(minableBlockId==3 || minableBlockId==153)
			{
				genOverrides(worldObj, rand, MPChunk_X, MPChunk_Z, minableBlockId);
			}
			if (generateBeforeCheck() == false)
			{
				//System.out.println(" 2.2: procede with gen"); /// for debugging315 56 298
				MPPrevX = MPChunk_X;
				MPPrevZ = MPChunk_Z;
				x_Chunk = MPChunk_X;
				z_Chunk = MPChunk_Z;
				MPPrevID = MPBlockID;
				MPPrevMeta = minableBlockMeta;
				mineGen = 1;
				subMineGen = 1;

				BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine[1.1].txt");
				BODFileOres = BODFile;
				BODbiomesFile = new File(configDirectory + "/" + versionNum + "BOD-biomes-Mine[1.1].txt");
				File f=new File(configDirectory + ""); 
				if(f.exists()==false)
				{
					f.mkdirs();
				}

				if(BODFile.exists()==false)
				{
					if(whatWorld=="/OverWorld")
					{
						writeBOD(BODFile);
					}
					if(whatWorld=="/Nether")
					{
						writeBODnether(BODFile);
					}
					//System.out.println("write BODprops"); /// for debugging
				}
				if(BODbiomesFile.exists()==false && whatWorld=="/OverWorld")
				{
					writeBODbiomes(BODbiomesFile);
					//System.out.println("write BODbiomes"); /// for debugging
				}      

				while(BODFile.exists())
				{
					//System.out.println(" 2.3: bod file exists, checking rarity random"); /// for debugging
					betterOreDistribution(x_Chunk, z_Chunk, MPBlockID, minableBlockMeta); // gather ore gen values from .txt

					if (rarity > 0){rarity = mPBiomeRarity(rarity, x_Chunk, z_Chunk, MPBlockID);} // retrieve rarity for this ore in this biome
					if (rarity == 1 || (rarity > 0 && rand.nextInt(rarity) == 0)) // use values
					{
						//System.out.println(" 2.3.1: rarity passed"); /// for debugging
						while(BODFile.exists())
						{                                      
							//System.out.println(" 2.3.2: other bod file works"); /// for debugging
							betterOreDistribution(x_Chunk, z_Chunk, MPBlockID, minableBlockMeta); // gather ore gen values from .txt
							//System.out.println("makin a mine at " + (MPChunk_X / 16) + "," + (MPChunk_Z / 16) + ", id " + MPBlockID + "." + minableBlockMeta + "-" + MPPrevID3 + "-" + MPPrevID4 + ", settings: R " + rarity + ", VS " + veinSi + ", VA " + veinAm + ", H " + height + ", D " + diameter + ", VD " + vDens + ", HD " + hDens); // used for debugging
							//System.out.println("generate veins"); /// for debugging
							if(subMineGen == 1){createMine(worldObj, rand, x_Chunk, z_Chunk);}
							else{createMineWithChance(worldObj, rand, x_Chunk, z_Chunk);}

							subMineGen++;
							BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine["+ mineGen +"."+ subMineGen +"].txt");
						}
					}
					subMineGen = 1;
					mineGen++;
					BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine["+ mineGen +"."+ subMineGen +"].txt");
				}
			}
			//else{System.out.println(" checked, and genned before!" + minableBlockId);}// for debugging
		}
		return true;
	}

	public int mPCalculateDensity(int oreDistance, float oreDensity) // returns the density value
	{

		int loopCount = 0;
		int densityValuePassInner = 0;
		int densityValuePass = 0;
		oreDensity = oreDensity * .01F;
		oreDensity = (oreDensity * (oreDistance >> 1)) + 1F;// establishes number of times to loop
		loopCount = (int)(oreDensity); //stores number of times to loop
		densityValuePassInner = ((oreDistance/loopCount)); // distance devided by number of times it will loop, establishes the number for randomization
		densityValuePassInner += (((oreDistance - (densityValuePassInner*loopCount))/loopCount));
		densityValuePass = 0;
		while (loopCount > 0) // loops to acumulate random values
		{
			densityValuePass = densityValuePass + rand.nextInt(densityValuePassInner); // acumulate randoms
			loopCount = loopCount - 1; // decriment loop
		}
		return densityValuePass; // return proccesed random value
	}

	public int mPBiomeRarity(int biomeRar, int xChunkBio, int zChunkBio, int MPMinableBlockId)
	{
		//worldChunkManager = worldObj.getWorldChunkManager();
		//BiomeGenBase biomegenbase = worldChunkManager.getBiomeGenAt(xChunkBio, zChunkBio);

		Properties props = new Properties();
		int biomeVals = rarity;
		String valPass = "1";
		String valPassB = "1";
		int inc1 = 1;
		try
		{
			BODbiomesFile = new File(configDirectory + "/" + versionNum + "BOD-Biomes-Mine["+ mineGen +"."+ subMineGen +"].txt"); // setup file // remove  the minecraft directory portion for multiplayer
			if(BODbiomesFile.exists())
			{
				props.load(new FileInputStream(BODbiomesFile));

				valPass = String.valueOf("OreID[" + MPMinableBlockId + "." + minableBlockMeta + "]-BiomeID[" + worldObj.getWorldChunkManager().getBiomeGenAt(xChunkBio, zChunkBio).biomeID + "]"); //  biomegenbase.biomeID

				if (props.getProperty(valPass)!= null) // make sure it exists first
				{
					valPassB = props.getProperty(valPass); // get new rarity value
					biomeVals = Integer.valueOf(valPassB);
				}
			}
		}
		catch(IOException j)
		{
			System.out.println("couldnt load BODbiomes"); /// for debugging
		}
		/*try
															{
																			File List = new File(Minecraft.getMinecraftDir() + "/BODprops/(V2.4)BiomesList.txt"); // setup file // remove  the minecraft directory portion for multiplayer
																			props.load(new FileInputStream(List));
															}
											catch(IOException g)
											{
															try // Write Biome ID reference
																			{
																							BufferedWriter out = new BufferedWriter(new FileWriter(BODFile));
																							inc1 = 0;
																							out.write("This file acts as a reference to all Biomes used currently." + "\r\n"  + "To refresh this list, delete this text file, and a new one will be made with updated biomes when new generation takes place");
																							while (inc1 <= 255)
																							{
																							if(biomegenbase.biomeList[inc1] == null){ out.write("ID" + inc1 + ": unused ID" + "\r\n");}
																							else
																											{
																															out.write("Biome ID " + inc1 + ": " + biomegenbase.biomeList[inc1] + "\r\n");
																											}
																							inc1++;
																							}
																							out.close();
																			}
															catch(IOException k){}
									}//*//*
		if( valPass != null){biomeRar =  biomeVals ;}
		else {biomeRar = rarity;}
		return biomeRar;

	}

	public boolean betterOreDistribution(int xChunk, int zChunk, int MPMinableBlockId, int MPMinableBlockMeta)
	{
		//System.out.println("assigning variables"); /// for debugging
		rarity = 2; // make sure all these dont have garbage data in them, for debugging mostly
		veinSi = 2;
		veinAm = 2;
		height = 2;
		mineHeight = 2;
		diameter = 2;
		vDens = 2;
		hDens = 2;
		useMarcoVeins = false;
		String valPass = "1";

		Properties props = new Properties();
		//try retrieve data from file
		try
		{

			BODFile = new File(configDirectory + "/" + versionNum + "BOD-Mine["+ mineGen +"."+ subMineGen +"].txt");
			if(BODFile.exists())
			{
				props.load(new FileInputStream(BODFile));
				// assign value to ore variable only if it is not null

				String valPass1 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "Rarity");
				String valPass2 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "VeinSize");
				String valPass3 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "VeinAmount");
				String valPass4 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "Height");
				String valPass5 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "VerticalShift");
				String valPass6 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "Diameter");
				String valPass7 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "VerticalDensity");
				String valPass8 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "HorizontalDensity");
				String valPass9 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "GenOreInBlockID");
				String valPass10 = String.valueOf(MPMinableBlockId + "." + MPMinableBlockMeta + "UseMarcoVeins");
				//System.out.println(MPMinableBlockId + "." + MPMinableBlockMeta + "HorizontalDensity"); /// for debugging

				if(mineGen == 1 && subMineGen == 1 )
				{
					if (props.getProperty(valPass1)== null || props.getProperty(valPass2)== null || props.getProperty(valPass3)== null || props.getProperty(valPass4)== null || props.getProperty(valPass5)== null || props.getProperty(valPass6)== null || props.getProperty(valPass7)== null || props.getProperty(valPass9)== null || props.getProperty(valPass10)== null)
					{
						try
						{

							BufferedWriter out = new BufferedWriter(new FileWriter(BODFile,true));
							out.write("#NewBlock" + MPMinableBlockId + "." + MPMinableBlockMeta + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "Rarity=50" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "VeinSize=10" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "VeinAmount=70" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "Height=95" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "VerticalShift=0" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "Diameter=48" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "VerticalDensity=10" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "HorizontalDensity=10" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "GenOreInBlockID=1" + "\r\n");
							out.write(MPMinableBlockId + "." + MPMinableBlockMeta + "UseMarcoVeins=false" + "\r\n" + "\r\n");
							out.close();
							//System.out.println(MPMinableBlockId + "." + MPMinableBlockMeta + "HorizontalDensity=10"); /// for debugging
						}
						catch (IOException h)
						{
							System.out.println("couldnt write in new ore settings for ore" + MPMinableBlockId + "." + MPMinableBlockMeta); /// for debugging
						}
					}
				}
				if (props.getProperty(valPass1)!= null){
					valPass = props.getProperty(valPass1); // rarity
					rarity = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass2) != null){
					valPass = props.getProperty(valPass2); // size
					veinSi = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass3) != null){
					valPass = props.getProperty(valPass3); // amount
					veinAm = Integer.valueOf(valPass) ;      }

				if (props.getProperty(valPass4) != null){
					valPass = props.getProperty(valPass4); // height
					height = Integer.valueOf(valPass) ;      }

				if (props.getProperty(valPass5) != null){
					valPass = props.getProperty(valPass5); // mineHeight
					mineHeight = Integer.valueOf(valPass) ;      }

				if (props.getProperty(valPass6) != null){
					valPass = props.getProperty(valPass6); // diameter
					diameter = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass7) != null){
					valPass = props.getProperty(valPass7); // vertical dense.
					vDens = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass8) != null){
					valPass = props.getProperty(valPass8); // horiz. dense.
					hDens = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass9) != null){
					valPass = props.getProperty(valPass9); // Gen. in block
					genInBlock = Integer.valueOf(valPass) ; }

				if (props.getProperty(valPass10) != null){
					valPass = props.getProperty(valPass10); // marco veins?
					useMarcoVeins = Boolean.valueOf(valPass) ; }
			}
			//else{System.out.println("couldnt assign variables, BODfile doesnt exist: " + BODFile);} /// for debugging}
		}                              

		//catch exception in case properties file does not exist
		catch(IOException e)
		{
			System.out.println("assigning variables had an exception!!!"); /// for debugging
		}  
		// all variables set, continue to generate
		return true;
	}

	public boolean BODgenerateVein(World world, Random rand, int parX, int parY, int parZ, int xyz)
	{
		//==========================================mp mod
		int posX = parX;
		int posY = parY;
		int posZ = parZ;
		int tempPosX =0;
		int tempPosY =0;
		int tempPosZ =0;
		int posX2 = 0;
		int posY2 = 0;
		int posZ2 = 0;
		int directionX =0;
		int directionY =0;
		int directionZ =0;
		int directionX2 = 0;
		int directionY2 = 0;
		int directionZ2 = 0;
		int directionX3 =0;
		int directionY3 =0;
		int directionZ3 =0;
		int directionChange =0;
		int directionChange2 =0;
		int blocksToUse = xyz;//input number of blocks per vein
		int blocksToUse2 =0;
		for(int blocksMade=0; blocksMade <= blocksToUse;) // make veins
		{
			blocksToUse2 = 1 + (blocksToUse/30);
			directionChange = rand.nextInt(6);
			directionX = rand.nextInt(2);
			directionY = rand.nextInt(2);
			directionZ = rand.nextInt(2);

			for(int blocksMade1 = 0; blocksMade1 <= blocksToUse2; ) // make branch
			{
				if(directionX == 0 && directionChange != 1){posX = posX + rand.nextInt(2);}
				if(directionX == 1 && directionChange != 1){posX = posX - rand.nextInt(2);}
				if(directionY == 0 && directionChange != 2){posY = posY + rand.nextInt(2);}
				if(directionY == 1 && directionChange != 2){posY = posY - rand.nextInt(2);}
				if(directionZ == 0 && directionChange != 3){posZ = posZ + rand.nextInt(2);}
				if(directionZ == 1 && directionChange != 3){posZ = posZ - rand.nextInt(2);}
				if(rand.nextInt(4) == 0){
					posX2 = posX2 + rand.nextInt(2);
					posY2 = posY2 + rand.nextInt(2);
					posZ2 = posZ2 + rand.nextInt(2);
					posX2 = posX2 - rand.nextInt(2);
					posY2 = posY2 - rand.nextInt(2);
					posZ2 = posZ2 - rand.nextInt(2);
				}
				if(rand.nextInt(3) == 0) // make sub-branch
				{
					posX2 = posX;
					posY2 = posY;
					posZ2 = posZ;

					directionX2 = rand.nextInt(2);
					directionY2 = rand.nextInt(2);
					directionZ2 = rand.nextInt(2);
					directionChange2 = rand.nextInt(6);
					if(directionX2 == 0 && directionChange2 != 0){posX2 = posX2 + rand.nextInt(2);}
					if(directionY2 == 0 && directionChange2 != 1){posY2 = posY2 + rand.nextInt(2);}
					if(directionZ2 == 0 && directionChange2 != 2){posZ2 = posZ2 + rand.nextInt(2);}
					if(directionX2 == 1 && directionChange2 != 0){posX2 = posX2 - rand.nextInt(2);}
					if(directionY2 == 1 && directionChange2 != 1){posY2 = posY2 - rand.nextInt(2);}
					if(directionZ2 == 1 && directionChange2 != 2){posZ2 = posZ2 - rand.nextInt(2);}



					for(int blocksMade2 = 0; blocksMade2 <= (1 +(blocksToUse2/5)); )
					{

						if(directionX2 == 0 && directionChange2 != 0){posX2 = posX2 + rand.nextInt(2);}
						if(directionY2 == 0 && directionChange2 != 1){posY2 = posY2 + rand.nextInt(2);}
						if(directionZ2 == 0 && directionChange2 != 2){posZ2 = posZ2 + rand.nextInt(2);}
						if(directionX2 == 1 && directionChange2 != 0){posX2 = posX2 - rand.nextInt(2);}
						if(directionY2 == 1 && directionChange2 != 1){posY2 = posY2 - rand.nextInt(2);}
						if(directionZ2 == 1 && directionChange2 != 2){posZ2 = posZ2 - rand.nextInt(2);}
						if(world.getBlockId(posX, posY, posZ) == Block.stone.blockID || world.getBlockId(posX, posY, posZ) == 87)
						{
							world.setBlock(posX, posY, posZ, MPBlockID, minableBlockMeta, 2);
						}
						blocksMade++;
						blocksMade1++;
						blocksMade2++;
					}
				}

				if(world.getBlockId(posX, posY, posZ) == Block.stone.blockID || world.getBlockId(posX, posY, posZ) == 87)
				{
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

	public boolean BODgenerate(World world, Random rand, int par3, int par4, int par5, int xyz)
	{

		//==========================================mp mod
		numberOfBlocks = xyz; //input number of blocks per vein

		//==========================================mp mod
		float var6 = rand.nextFloat() * (float)Math.PI;
		double var7 = (double)((float)(par3 + 8) + MathHelper.sin(var6) * (float)numberOfBlocks / 8.0F);
		double var9 = (double)((float)(par3 + 8) - MathHelper.sin(var6) * (float)numberOfBlocks / 8.0F);
		double var11 = (double)((float)(par5 + 8) + MathHelper.cos(var6) * (float)numberOfBlocks / 8.0F);
		double var13 = (double)((float)(par5 + 8) - MathHelper.cos(var6) * (float)numberOfBlocks / 8.0F);
		double var15 = (double)(par4 + rand.nextInt(3) - 2);
		double var17 = (double)(par4 + rand.nextInt(3) - 2);

		for (int var19 = 0; var19 <= numberOfBlocks; ++var19)
		{
			double var20 = var7 + (var9 - var7) * (double)var19 / (double)numberOfBlocks;
			double var22 = var15 + (var17 - var15) * (double)var19 / (double)numberOfBlocks;
			double var24 = var11 + (var13 - var11) * (double)var19 / (double)numberOfBlocks;
			double var26 = rand.nextDouble() * (double)this.numberOfBlocks / 16.0D;
			double var28 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)numberOfBlocks) + 1.0F) * var26 + 1.0D;
			double var30 = (double)(MathHelper.sin((float)var19 * (float)Math.PI / (float)numberOfBlocks) + 1.0F) * var26 + 1.0D;
			int var32 = MathHelper.floor_double(var20 - var28 / 2.0D);
			int var33 = MathHelper.floor_double(var22 - var30 / 2.0D);
			int var34 = MathHelper.floor_double(var24 - var28 / 2.0D);
			int var35 = MathHelper.floor_double(var20 + var28 / 2.0D);
			int var36 = MathHelper.floor_double(var22 + var30 / 2.0D);
			int var37 = MathHelper.floor_double(var24 + var28 / 2.0D);

			for (int var38 = var32; var38 <= var35; ++var38)
			{
				double var39 = ((double)var38 + 0.5D - var20) / (var28 / 2.0D);

				if (var39 * var39 < 1.0D)
				{
					for (int var41 = var33; var41 <= var36; ++var41)
					{
						double var42 = ((double)var41 + 0.5D - var22) / (var30 / 2.0D);

						if (var39 * var39 + var42 * var42 < 1.0D)
						{
							for (int var44 = var34; var44 <= var37; ++var44)
							{
								double var45 = ((double)var44 + 0.5D - var24) / (var28 / 2.0D);

								Block block = Block.blocksList[world.getBlockId(var38, var41, var44)];
								//if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && (block != null && block.isGenMineableReplaceable(par1World, k2, l2, i3, field_94523_c)))
								if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && (block != null && (world.getBlockId(var38, var41, var44) == genInBlock)))
								{
									//world.setBlockAndMetadata(var38, var41, var44, minableBlockId, minableBlockMeta);
									world.setBlock(var38, var41, var44, minableBlockId, minableBlockMeta, 2);
									//System.out.println("block at " + var38 +" "+var41+" "+var44); /// for debugging
								}
							}
						}
					}
				}
			}
		}
		//System.out.println("a vein was placed " + minableBlockId + "." + minableBlockMeta+ " at " + par3 +" "+par4+" "+par5); /// for debugging
		return true;
	}

	public boolean writeBODoverrides(File writeTo)
	{
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("# format:" + "\r\n");
			out.write("# X.add_new_ore=Y:Z" + "\r\n");
			out.write("# X = the override number, start counting from 1. once the progam doesnt see the next number it stops, so dont do 1, 2, 3, 5 because it will stop at 3" + "\r\n");
			out.write("# Y = the block ID" + "\r\n");
			out.write("# Z = the block meta data, almost all vanilla blocks are 0, with exceptions like colored wool, and half slabs" + "\r\n");
			out.write("# Note: If you enter an ID that isn't a block, then it will get stuck on Building terrain, or crash. This is an override after all" + "\r\n");
			out.write("1.add_new_ore=129:0" + "\r\n");
			out.close();
		}
		catch (IOException f)
		{
			System.out.println("could not write BODoverrides"); /// for debugging
		}
		return true;
	}
	public boolean writeBODnetherOverrides(File writeTo)
	{
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("# format:" + "\r\n");
			out.write("# X.add_new_ore=Y:Z" + "\r\n");
			out.write("# X = the override number, start counting from 1. once the progam doesnt see the next number it stops, so dont do 1, 2, 3, 5 because it will stop at 3" + "\r\n");
			out.write("# Y = the block ID" + "\r\n");
			out.write("# Z = the block meta data, almost all vanilla blocks are 0, with exceptions like colored wool, and half slabs" + "\r\n");
			out.write("# Note: If you enter an ID that isn't a block, then it will get stuck on Building terrain, or crash. This is an override after all" + "\r\n");
			out.close();
		}
		catch (IOException f)
		{
			System.out.println("could not write BODoverrides"); /// for debugging
		}
		return true;
	}
	public boolean writeBOD(File writeTo)
	{
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("#see forum for more instructions: http://www.minecraftforum.net/topic/330485-10-marcopolos-mods/" + "\r\n");
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
			out.write("3.0GenOreInBlockID=1"+ "\r\n");
			out.write("3.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Gravel" + "\r\n"); // gravel
			out.write("13.0Rarity=50" + "\r\n");
			out.write("13.0VeinSize=30" + "\r\n");
			out.write("13.0VeinAmount=17" + "\r\n");
			out.write("13.0Height=128" + "\r\n");
			out.write("13.0VerticalShift=0" + "\r\n");
			out.write("13.0Diameter=32" + "\r\n");
			out.write("13.0VerticalDensity=1" + "\r\n");
			out.write("13.0HorizontalDensity=1" + "\r\n");
			out.write("13.0GenOreInBlockID=1"+ "\r\n");
			out.write("13.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Gold" + "\r\n"); // gold
			out.write("14.0Rarity=140" + "\r\n");
			out.write("14.0VeinSize=8" + "\r\n");
			out.write("14.0VeinAmount=135" + "\r\n");
			out.write("14.0Height=80" + "\r\n");
			out.write("14.0VerticalShift=0" + "\r\n");
			out.write("14.0Diameter=60" + "\r\n");
			out.write("14.0VerticalDensity=20" + "\r\n");
			out.write("14.0HorizontalDensity=10" + "\r\n");
			out.write("14.0GenOreInBlockID=1"+ "\r\n");
			out.write("14.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Iron" + "\r\n"); // Iron
			out.write("15.0Rarity=75" + "\r\n");
			out.write("15.0VeinSize=8" + "\r\n");
			out.write("15.0VeinAmount=220" + "\r\n");
			out.write("15.0Height=80" + "\r\n");
			out.write("15.0VerticalShift=0" + "\r\n");
			out.write("15.0Diameter=65" + "\r\n");
			out.write("15.0VerticalDensity=15" + "\r\n");
			out.write("15.0HorizontalDensity=15" + "\r\n");
			out.write("15.0GenOreInBlockID=1"+ "\r\n");
			out.write("15.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Coal" + "\r\n"); // coal
			out.write("16.0Rarity=80" + "\r\n");
			out.write("16.0VeinSize=7" + "\r\n");
			out.write("16.0VeinAmount=330" + "\r\n");
			out.write("16.0Height=6" + "\r\n");
			out.write("16.0VerticalShift=45" + "\r\n");
			out.write("16.0Diameter=70" + "\r\n");
			out.write("16.0VerticalDensity=85" + "\r\n");
			out.write("16.0HorizontalDensity=10" + "\r\n");
			out.write("16.0GenOreInBlockID=1"+ "\r\n");
			out.write("16.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Lapis" + "\r\n"); // lapis
			out.write("21.0Rarity=225" + "\r\n");
			out.write("21.0VeinSize=8" + "\r\n");
			out.write("21.0VeinAmount=200" + "\r\n");
			out.write("21.0Height=50" + "\r\n");
			out.write("21.0VerticalShift=0" + "\r\n");
			out.write("21.0Diameter=70" + "\r\n");
			out.write("21.0VerticalDensity=20" + "\r\n");
			out.write("21.0HorizontalDensity=10" + "\r\n");
			out.write("21.0GenOreInBlockID=1"+ "\r\n");
			out.write("21.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Diamond" + "\r\n"); // daimond
			out.write("56.0Rarity=160" + "\r\n");
			out.write("56.0VeinSize=8" + "\r\n");
			out.write("56.0VeinAmount=220" + "\r\n");
			out.write("56.0Height=70" + "\r\n");
			out.write("56.0VerticalShift=0" + "\r\n");
			out.write("56.0Diameter=70" + "\r\n");
			out.write("56.0VerticalDensity=20" + "\r\n");
			out.write("56.0HorizontalDensity=10" + "\r\n");
			out.write("56.0GenOreInBlockID=1"+ "\r\n");
			out.write("56.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Redstone" + "\r\n"); // redstone
			out.write("73.0Rarity=110" + "\r\n");
			out.write("73.0VeinSize=12" + "\r\n");
			out.write("73.0VeinAmount=160" + "\r\n");
			out.write("73.0Height=12" + "\r\n");
			out.write("73.0VerticalShift=0" + "\r\n");
			out.write("73.0Diameter=160" + "\r\n");
			out.write("73.0VerticalDensity=20" + "\r\n");
			out.write("73.0HorizontalDensity=5"+ "\r\n");
			out.write("73.0GenOreInBlockID=1"+ "\r\n");
			out.write("73.0UseMarcoVeins=false" + "\r\n"  + "\r\n");

			out.write("#Emerald" + "\r\n"); // emerald
			out.write("129.0Rarity=110" + "\r\n");
			out.write("129.0VeinSize=3" + "\r\n");
			out.write("129.0VeinAmount=600" + "\r\n");
			out.write("129.0Height=60" + "\r\n");
			out.write("129.0VerticalShift=0" + "\r\n");
			out.write("129.0Diameter=60" + "\r\n");
			out.write("129.0VerticalDensity=20" + "\r\n");
			out.write("129.0HorizontalDensity=5"+ "\r\n");
			out.write("129.0GenOreInBlockID=1"+ "\r\n");
			out.write("129.0UseMarcoVeins=false" + "\r\n"  + "\r\n");
			out.close();
		}
		catch (IOException f)
		{
			System.out.println("could not write BODprops"); /// for debugging
		}
		return true;
	}
	public boolean writeBODnether(File writeTo)
	{
		try // write BOD(<version>).txt
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("#see forum for more instructions: http://www.minecraftforum.net/topic/330485-10-marcopolos-mods/" + "\r\n");
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
			out.write("153.0GenOreInBlockID=87"+ "\r\n");
			out.write("153.0UseMarcoVeins=true" + "\r\n"  + "\r\n");
			out.close();
		}
		catch (IOException f)
		{
			System.out.println("could not write BODprops"); /// for debugging
		}
		return true;
	}
	public boolean writeBODbiomes(File writeTo)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(writeTo));
			out.write("# 3.0=dirt, 13.0=gravel, 14.0=gold, 15.0=iron, 16.0=coal, 21.0=lapis, 56.0=diamond, 73.0=redstone, 129.0=emerald" + "\r\n");
			out.write("\r\n");
			out.write("# ------------------------------------------------------------"+"\r\n");
			out.write("# format is OreID[X.Xo]-BiomeID[Y]=Z" + "\r\n");
			out.write("# X = Ore ID"+"\r\n");
			out.write("# Xo = meta data for Ore ID"+"\r\n");
			out.write("# Y = Biome ID"+"\r\n");
			out.write("# Z = the rarity you want for that ore in that biome"+"\r\n");
			out.write("# ------------------------------------------------------------"+"\r\n");
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
		}
		catch(IOException i)
		{
			System.out.println("could not write BODbiomes!!!"); /// for debugging
		}
		return true;
	}//*/
}