package ssw.cs555.stevens.edu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class TeamFive {
	// lists to hold data
	private static HashMap<String, Family> families = new HashMap<String, Family>();
	private static HashMap<String, Individual> individuals = new HashMap<String, Individual>();
	private static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	private static String fileName = null;
	private static ArrayList<Individual> dupInd = new ArrayList<Individual>();
	private static ArrayList<Family> dupFam = new ArrayList<Family>();

	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("Enter the Input File Path and Filename: ");
		try {
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			String fileName = bufferRead.readLine();
			createNewOutputFile();
			readAndParseFile(fileName);
			printMaps();

			// ****Sprint 1****

			// US01 - Kuo Fan
			datesBeforeCurrentdate(individuals, families);
			// US02 - Jason Sarwar
			birthBeforeMarriage(individuals, families);
			// US03 - Jason Sarwar
			birthBeforeDeath(individuals);
			// US04 - Kuo Fan
			Marriagebeforedivorce(individuals, families);
			// US05 - Patrick Hill
			marriageBeforeDeath(families);
			// US06 - Patrick Hill
			divorceBeforeDeath(families);
			// US07 - Xuanhong Shen
			lessThan150(individuals);
			// US08 - Xuanhong Shen
			birthBeforeMarriageofParents(individuals, families);

			// ****Sprint 2****

			// US09 - Xuanhong Shen
			birthBeforeDeathofParents(individuals, families);
			// US10 - Xuanhong Shen
			marriageAfterFourteen(individuals, families);
			// US11 - Jason Sarwar
			// TODO
			// US12 - Jason Sarwar
			parentsNotTooOld(individuals, families);
			// US13 - Patrick Hill
			siblingSpacing(families);
			// US14 - Kuo Fan
			// TODO
			// US15 - Patrick Hill
			fewerThanFifteenSiblings(families);
			// US16 - Kuo Fan
			// TODO

			// ****Sprint 3****

			// US18 - Jason Sarwar
			siblingsShouldNotMarry(individuals, families);
			// US19 - Xuanhong Shen
			cousinsNotMarry(individuals, families);
			// US20 - Patrick Hill
			auntsAndUncles(families);
			// US21 - Kuo Fan
			currGenForRole(individuals, families);
			// US22 - Xuanhong Shen
			uniqueID(individuals, families);
			// US23 - Patrick Hill
			uniqueNameAndBirthdate(individuals);
			// US24 - Jason Sarwar
			uniqueFamiliesBySpouses(individuals, families);
			// US29 - Kuo Fan
			listDeceased(individuals);

			// ****Sprint 4****
			// US31 - Jason Sarwar
			listLivingSingle(individuals, families)
			// US33 - Kuo Fan
		    listOrphans(individuals, families);

			// US34 - Kuo Fan
			listLargeAgeDiff(individuals, families);

			// US35 - Xuanhong Shen
			recentBirth(individuals);
			// US36 - Xuanhong Shen
			recentDeath(individuals);
			// US37 - Jason Sarwar
			listRecentSurvivors(individuals, families);
			// US38 - Patrick Hill
			upcomingBirthdays(individuals);
			// US39 - Patrick Hill
			upcomingAnniversaries(families);

		} catch (FileNotFoundException ex) {
			System.out.println("File Not Found. Please Check Path and Filename");
			main(null);
		}
	}

	static void readAndParseFile(String fileName) throws IOException {
		// valid tags
		String[] validTags = { "INDI", "NAME", "SEX", "BIRT", "DEAT", "FAMC", "FAMS", "FAM", "MARR", "HUSB", "WIFE",
				"CHIL", "DIV", "DATE", "HEAD", "TRLR", "NOTE" };
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = br.readLine();
		// loop through each line of the file
		writeToFile(
				"*************************************************LINES****************************************************");

		while (line != null) {
			writeToFile(line);
			String[] parts = line.split(" ");
			writeToFile("Level: " + parts[0]);
			if (Arrays.asList(validTags).contains(parts[1])) {
				writeToFile("TAG: " + parts[1] + "\n");
				line = br.readLine();
			} else {
				if (parts[0].equals("0") && Arrays.asList(validTags).contains(parts[2])) {
					writeToFile("TAG: " + parts[2] + "\n");
					// if it's an INDI record
					if (parts[2].equals("INDI")) {
						Individual indi = new Individual(parts[1]);
						String individualParts = br.readLine();
						do {
							String[] indParts = individualParts.split(" ");
							if (indParts[1].equals("NAME"))
								indi.setName(indParts[2] + " " + indParts[3].substring(1, indParts[3].length() - 1));
							if (indParts[1].equals("SEX"))
								indi.setSex(indParts[2]);
							if (indParts[1].equals("FAMS"))
								indi.setSpouseOf(indParts[2]);
							if (indParts[1].equals("FAMC"))
								indi.setChildOf(indParts[2]);
							if (indParts[1].equals("BIRT")) {
								individualParts = br.readLine();
								indParts = individualParts.split(" ");
								String month = getMonth(indParts[3]);
								indi.setBirth(month + "/" + indParts[2] + "/" + indParts[4]);
							}
							if (indParts[1].equals("DEAT") && indParts[2].equals("Y")) {
								individualParts = br.readLine();
								indParts = individualParts.split(" ");
								String month = getMonth(indParts[3]);
								indi.setDeath(month + "/" + indParts[2] + "/" + indParts[4]);
							}
							individualParts = br.readLine();
						} while (!individualParts.startsWith("0"));
						line = individualParts;
						if (!individuals.containsKey(indi.getId())) {
							individuals.put(indi.getId(), indi);
						} else {
							dupInd.add(indi);
						}
					} else if (parts[2].equals("FAM")) {
						ArrayList<String> children = new ArrayList<String>();
						Family fam = new Family(parts[1]);
						String familyParts = br.readLine();
						do {
							String[] indFamParts = familyParts.split(" ");
							if (indFamParts[1].equals("HUSB"))
								fam.setHusb(indFamParts[2]);
							if (indFamParts[1].equals("WIFE"))
								fam.setWife(indFamParts[2]);
							if (indFamParts[1].equals("CHIL")) {
								children.add(indFamParts[2]);
								fam.setChild(children);
							}
							if (indFamParts[1].equals("MARR")) {
								familyParts = br.readLine();
								indFamParts = familyParts.split(" ");
								String month = getMonth(indFamParts[3]);
								fam.setMarriage(month + "/" + indFamParts[2] + "/" + indFamParts[4]);
							}
							if (indFamParts[1].equals("DIV")) {
								familyParts = br.readLine();
								indFamParts = familyParts.split(" ");
								String month = getMonth(indFamParts[3]);
								fam.setDivorce(month + "/" + indFamParts[2] + "/" + indFamParts[4]);
							}
							familyParts = br.readLine();
						} while (!familyParts.startsWith("0"));
						if (!families.containsKey(fam.getId())) {
							families.put(fam.getId(), fam);
						} else {
							dupFam.add(fam);
						}
						line = familyParts;
					}
				}
				// if the tag is not valid print invalid tag
				else {
					writeToFile("TAG: INVALID\n");
					line = br.readLine();
				}
			}
		}
		writeToFile(// TODO
				"**********************************************************************************************************\n");

		br.close();
	}

	public static void printMaps() throws FileNotFoundException, IOException {
		// This method just prints out the values of both hashmaps
		Map<String, Individual> indMap = new TreeMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		writeToFile(
				"*********************************************INDIVIDUALS**************************************************");
		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			writeToFile(indEntry.getKey() + " - " + " Name: " + ind.getName() + ", Sex: " + ind.getSex() + ", DOB: "
					+ ind.getBirth() + ", DOD: " + ind.getDeath() + ", Spouse of: " + ind.getSpouseOf() + ", Child of: "
					+ ind.getChildOf());

		}
		writeToFile(
				"**********************************************************************************************************\n");

		System.out.println("\n");
		// loop through the family collection and print out
		Map<String, Family> famMap = new TreeMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		writeToFile(
				"*********************************************FAMILIES*****************************************************");
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			writeToFile(famEntry.getKey() + " - Husband: " + fam.getHusb() + ", Wife: " + fam.getWife() + ", Children: "
					+ fam.getChild() + ", Marriage Date: " + fam.getMarriage() + ", Divorce Date: " + fam.getDivorce());
		}
		writeToFile(
				"**********************************************************************************************************\n");

	}

	public static void createNewOutputFile() throws IOException {
		System.out.println("Enter Output File Path: ");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		String fp = bufferRead.readLine();
		Path filePath = Paths.get(fp);
		if (Files.exists(filePath)) {
			fileName = filePath + "\\output.txt";
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, false)))) {
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
		} else {
			System.out.println("The Output Path You Entered Does Not Exist.  Please Try Again");
			createNewOutputFile();
		}
	}

	public static void writeToFile(String output) throws FileNotFoundException, IOException {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)))) {
			out.println(output);
			System.out.println(output);
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}

	}

	public static String getMonth(String month) {
		String numMonth = null;
		switch (month) {
		case "JAN":
			numMonth = "01";
			break;
		case "FEB":
			numMonth = "02";
			break;
		case "MAR":
			numMonth = "03";
			break;
		case "APR":
			numMonth = "04";
			break;
		case "MAY":
			numMonth = "05";
			break;
		case "JUN":
			numMonth = "06";
			break;
		case "JUL":
			numMonth = "07";
			break;
		case "AUG":
			numMonth = "08";
			break;
		case "SEP":
			numMonth = "09";
			break;
		case "OCT":
			numMonth = "10";
			break;
		case "NOV":
			numMonth = "11";
			break;
		case "DEC":
			numMonth = "12";
			break;
		}
		return numMonth;
	}

	// USER STORY METHODS

	static void marriageBeforeDeath(HashMap<String, Family> families) throws FileNotFoundException, IOException {
		// Sprint 1 Patrick Hill User Story US05 - Marriage Date Before Death
		Date marriageDate = null;
		Date deathDate = null;
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			try {
				marriageDate = sdf.parse(fam.getMarriage());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Individual ind = individuals.get(fam.getHusb());
			Individual ind2 = individuals.get(fam.getWife());
			if ((fam.getHusb().equals(ind.getId()) && ind.getDeath() != null)
					|| (fam.getWife().equals(ind2.getId()) && ind2.getDeath() != null)) {
				try {
					if (ind.getDeath() != null) {
						deathDate = sdf.parse(ind.getDeath());
						if (marriageDate.after(deathDate)) {
							writeToFile(
									"***************************ERROR: User Story US05: Death before Marriage Date*****************************\nFamily ID: "
											+ fam.getId() + "\nIndividual: " + ind.getId() + " - " + ind.getName()
											+ " Has been dead since: " + ind.getDeath() + " And they were married on: "
											+ fam.getMarriage()
											+ "\n**********************************************************************************************************\n");
						}
					}
					if (ind2.getDeath() != null) {
						deathDate = sdf.parse(ind2.getDeath());
						if (marriageDate.after(deathDate)) {
							writeToFile(
									"***************************ERROR: User Story US05: Death before Marriage Date*****************************\nFamily ID: "
											+ fam.getId() + "\nIndividual: " + ind2.getId() + " - " + ind2.getName()
											+ " Has been dead since: " + ind2.getDeath() + " And they were married on: "
											+ fam.getMarriage()
											+ "\n**********************************************************************************************************\n");
						}
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static void divorceBeforeDeath(HashMap<String, Family> families) throws FileNotFoundException, IOException {
		// Sprint 1 Patrick Hill User Story US06 - Divorce Date Before Death
		Date divorceDate = null;
		Date deathDate = null;
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			// Get the date of birth
			try {
				divorceDate = sdf.parse(fam.getMarriage());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Individual ind = individuals.get(fam.getHusb());
			Individual ind2 = individuals.get(fam.getWife());
			if (fam.getDivorce() != null) {
				if ((fam.getHusb().equals(ind.getId()) && ind.getDeath() != null)
						|| (fam.getWife().equals(ind2.getId()) && ind2.getDeath() != null)) {
					try {
						if (ind.getDeath() != null) {
							deathDate = sdf.parse(ind.getDeath());
							if (divorceDate.after(deathDate)) {
								writeToFile(
										"***************************ERROR: User Story US06: Death before Divorce Date******************************\nFamily ID: "
												+ fam.getId() + "\nIndividual: " + ind.getId() + " - " + ind.getName()
												+ " Has been dead since: " + ind.getDeath()
												+ " And they were divorced on: " + fam.getDivorce()
												+ "\n**********************************************************************************************************\n");
							}
						}
						if (ind2.getDeath() != null) {
							deathDate = sdf.parse(ind2.getDeath());
							if (divorceDate.after(deathDate)) {
								writeToFile(
										"***************************ERROR: User Story US06: Death before Divorce Date******************************\nFamily ID: "
												+ fam.getId() + "\nIndividual: " + ind2.getId() + " - " + ind2.getName()
												+ " Has been dead since: " + ind2.getDeath()
												+ " And they were divorced on: " + fam.getDivorce()
												+ "\n**********************************************************************************************************\n");
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	static void lessThan150(HashMap<String, Individual> individuals) throws FileNotFoundException, IOException {
		// Sprint 1 XuanhongShen User Story US07
		// Iterate through the collection of individuals.
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			// Get the date of birth
			int date_of_birth = Integer.parseInt(indi.getBirth().split("/")[2]);
			/*
			 * Check if the person is alive. If so, compare the birth date and
			 * date of today; If not, compare the birth date with death date.
			 */
			if (indi.getDeath() == null) {
				// use calendar to get the date of today
				Calendar ca = Calendar.getInstance();
				int year = ca.get(Calendar.YEAR);
				int diff = year - date_of_birth;
				if (diff > 150) {
					writeToFile(
							"***************************ERROR: User Story US07: Less Than 150 Years old********************************\nIndividual: "
									+ indi.getId() + " - " + indi.getName() + " is greater than 150 years old.\nDOB: "
									+ indi.getBirth()
									+ "\n**********************************************************************************************************\n");
				}
			} else {
				int date_of_death = Integer.parseInt(indi.getDeath().split("/")[2]);
				int diff = date_of_death - date_of_birth;
				if (diff > 150)
					writeToFile(
							"***************************ERROR: User Story US07: Less Than 150 Years old********************************\nIndividual: "
									+ indi.getId() + " - " + indi.getName()
									+ " The difference between their birthdate and the death is greater than 150 years.\n DOB: "
									+ indi.getBirth() + " DOD: " + indi.getDeath()
									+ "\n**********************************************************************************************************\n");
			}
		}
	}

	static void birthBeforeMarriageofParents(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 1 XuanhongShen User Story US08
		// Loop through the List of families.
		// for every family, loop through the list of children, check if
		// birthBeforeMarriage occurs
		Date marriageDate = null;
		Date divorceDate = null;
		Date birthDate = null;
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		// List<String> children = new ArrayList<String>();
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			try {
				marriageDate = sdf.parse(fam.getMarriage());
				if (fam.getChild() != null) {
					for (int i = 0; i < fam.getChild().size(); i++) {
						Individual indi = indMap.get(fam.getChild().get(i));
						birthDate = sdf.parse(indi.getBirth());
						if (birthDate.before(marriageDate)) {
							writeToFile(
									"***************************ERROR: User Story US08: Birth Before Marriage Date*****************************\nFamily ID: "
											+ fam.getId() + "\nIndividual: " + indi.getId() + ": " + indi.getName()
											+ " Has been born before parents' marriage\nDOB: " + indi.getBirth()
											+ " Parents Marriage Date: " + fam.getMarriage()
											+ "\n**********************************************************************************************************\n");
						}
						if (fam.getDivorce() != null) {
							divorceDate = sdf.parse(fam.getDivorce());
							if (birthDate.after(divorceDate)) {
								writeToFile(
										"***************************ERROR: User Story US08: Birth After Divorce Date*****************************\nFamily ID: "
												+ fam.getId() + "\nIndividual: " + indi.getId() + ": " + indi.getName()
												+ " Has been born after parents' divorce\nDOB: " + indi.getBirth()
												+ " Parents Divorce Date: " + fam.getDivorce()
												+ "\n*********************************************************************************************************\n");
							}
						}
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static void birthBeforeMarriage(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 1 Jason Sarwar User Story US02 - Marriage Date Before Death
		// Checks if individuals got married before birth
		Date marriageDate = null;
		Date birthDate = null;
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			try {
				marriageDate = sdf.parse(fam.getMarriage());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
			Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
			while (indEntries.hasNext()) {
				Map.Entry<String, Individual> indEntry = indEntries.next();
				Individual ind = indEntry.getValue();
				if ((fam.getHusb().equals(ind.getId()) && ind.getBirth() != null)
						|| (fam.getWife().equals(ind.getId()) && ind.getBirth() != null)) {
					try {
						birthDate = sdf.parse(ind.getBirth());
						if (birthDate.after(marriageDate)) {
							writeToFile(
									"***************************ERROR: User Story US02: Marriage before Birth Date*****************************\nFamily ID: "
											+ fam.getId() + "\nIndividual: " + ind.getId() + " - " + ind.getName()
											+ " got married before birth\nDOB: " + ind.getBirth() + " Marriage Date: "
											+ fam.getMarriage()
											+ "\n**********************************************************************************************************\n");
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	static void birthBeforeDeath(HashMap<String, Individual> individuals) throws FileNotFoundException, IOException {
		// Sprint 1 Jason Sarwar User Story US03
		// Loops through individuals and checks if the birth date is before the
		// death date
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			// Get the date of birth and date of death
			Date date_of_birth = null;
			Date date_of_death = null;
			try {
				date_of_birth = sdf.parse(indi.getBirth());
				if (indi.getDeath() != null)
					date_of_death = sdf.parse(indi.getDeath());
				else
					date_of_death = null;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Comparing the dates
			if (date_of_death != null)
				if (date_of_birth.compareTo(date_of_death) > 0) {
					writeToFile(
							"***************************ERROR: User Story US03: Birth Before Death*************************************\nIndividual: "
									+ indi.getId() + " - " + indi.getName() + " was born after death\nDOB: "
									+ indi.getBirth() + " DOD: " + indi.getDeath()
									+ "\n**********************************************************************************************************\n");
				}
		}
	}

	static void datesBeforeCurrentdate(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 1 KuoFan User Story US01
		Date nowTime = new Date(System.currentTimeMillis());
		String nowdate1 = sdf.format(nowTime);
		Date nowdate = null;
		Date marriageDate = null;
		Date divorceDate = null;
		Date birthDate = null;
		Date deathDate = null;
		try {
			nowdate = sdf.parse(nowdate1);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();

		// List<String> children = new ArrayList<String>();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			try {
				if (fam.getMarriage() != null) {
					marriageDate = sdf.parse(fam.getMarriage());
				}
				if (fam.getDivorce() != null) {
					divorceDate = sdf.parse(fam.getDivorce());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Individual ind = individuals.get(fam.getHusb());
			Individual ind2 = individuals.get(fam.getWife());
			if (marriageDate.after(nowdate)) {
				writeToFile(
						"***************************ERROR: User Story US01: Dates After Current Date*******************************\nFamily ID: "
								+ fam.getId() + "\nIndividual: " + ind.getId() + " - " + ind.getName()
								+ " Married Individual: " + ind2.getId() + " - " + ind2.getName()
								+ " After the current date\nMarried Date:" + fam.getMarriage()
								+ "\n**********************************************************************************************************\n");
			}
			if (fam.getDivorce() != null) {
				if (divorceDate.after(nowdate)) {
					writeToFile(
							"***************************ERROR: User Story US01: Dates After Current Date*******************************\nFamily ID: "
									+ fam.getId() + "\nIndividual: " + ind.getId() + " - " + ind.getName()
									+ " Divorced Individual: " + ind2.getId() + " - " + ind2.getName()
									+ " After the current date\nDivorced Date: " + fam.getDivorce()
									+ "\n**********************************************************************************************************\n");
				}
			}
		}
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = indMap.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			try {
				if (indi.getBirth() != null) {
					birthDate = sdf.parse(indi.getBirth());
				}
				if (indi.getDeath() != null) {
					deathDate = sdf.parse(indi.getDeath());
				}
				nowdate = sdf.parse(nowdate1);
				if (indi.getBirth() != null && indi.getDeath() != null) {
					if (birthDate.after(nowdate)) {
						writeToFile(
								"***************************ERROR: User Story US01: Dates After Current Date*******************************\nIndividual: "
										+ indi.getId() + ": " + indi.getName()
										+ " Was born after the current date\n Birthday: " + indi.getBirth()
										+ "\n**********************************************************************************************************\n");
					}
					if (deathDate.after(nowdate)) {
						writeToFile(
								"***************************ERROR: User Story US01: Dates After Current Date*******************************\nIndividual: "
										+ indi.getId() + ": " + indi.getName()
										+ " Died after the current date\n Date Of Death: " + indi.getDeath()
										+ "\n**********************************************************************************************************\n");
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static void Marriagebeforedivorce(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 1 KuoFan User Story US04
		Date marriageDate = null;
		Date divorceDate = null;
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			try {
				// check if the divorce exists, if not, there is no need to
				// check if marriage before the divorce.
				if (fam.getDivorce() != null) {
					divorceDate = sdf.parse(fam.getDivorce());
					if (fam.getMarriage() == null)
						System.out.println("Missing marriage date");
					marriageDate = sdf.parse(fam.getMarriage());
					if (divorceDate.before(marriageDate)) {
						writeToFile(
								"***************************ERROR: User Story US04: Marriage Before Divorce********************************\nFamily: "
										+ fam.getId() + "\nIndividual: " + fam.getHusb() + ": "
										+ indMap.get(fam.getHusb()).getName() + fam.getWife() + ": "
										+ indMap.get(fam.getWife()).getName()
										+ " marriage date before divorce date\nMarriage Date: " + fam.getMarriage()
										+ " Divorce Date: " + fam.getDivorce()
										+ "\n**********************************************************************************************************\n");
					}
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static void birthBeforeDeathofParents(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 2 Xuanhong Shen User Story US09
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> indFam = new HashMap<String, Family>(families);

		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		Date birthDate = null;
		Date deathofMom = null;
		Date deathofDad = null; // In this case, it is nine month after father's
								// death;
		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			if (ind.getChildOf() == null)
				continue;
			Family fam = indFam.get(ind.getChildOf());
			Individual dad = indMap.get(fam.getHusb());
			Individual mom = indMap.get(fam.getWife());
			try {
				birthDate = sdf.parse(ind.getBirth());
				if (mom.getDeath() != null) {
					deathofMom = sdf.parse(mom.getDeath());
					if (birthDate.after(deathofMom)) {
						writeToFile(
								"***************************ERROR: User Story US09: Birth Before Death of Parents****************************\n"
										+ "Individual ID: " + ind.getId() + ": " + ind.getName()
										+ "  was born after the death of mother "
										+ "\n************************************************************************************************************\n");
					}
				}
				if (dad.getDeath() != null) {
					// deathofDad = sdf.parse(dad.getDeath());
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(dad.getDeath()));
					c.add(Calendar.MONTH, 9);
					deathofDad = c.getTime();
					if (birthDate.after(deathofDad)) {
						writeToFile(
								"***************************ERROR: User Story US09: Birth Before Death of Parents**************************\n"
										+ "Individual ID: " + ind.getId() + ": " + ind.getName()
										+ "  was born after the death of father "
										+ "\n**********************************************************************************************************\n");
					}

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	static void marriageAfterFourteen(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 2 Xuanhong Shen User Story US09
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Date marriageDate = null;
		Date ageFourteenOfHusb = null;
		Date ageFourteenOfWife = null;
		// boolean flag = false;

		// Iterator<Map.Entry<String, Individual>> indEntries =
		// indMap.entrySet().iterator();
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			if (fam.getMarriage() != null) {
				try {
					marriageDate = sdf.parse(fam.getMarriage());
					Individual husb = indMap.get(fam.getHusb());
					Individual wife = indMap.get(fam.getWife());
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(husb.getBirth()));
					c.add(Calendar.YEAR, 14);
					ageFourteenOfHusb = c.getTime();
					c.setTime(sdf.parse(wife.getBirth()));
					c.add(Calendar.YEAR, 14);
					ageFourteenOfWife = c.getTime();
					if (marriageDate.before(ageFourteenOfWife) || marriageDate.before(ageFourteenOfHusb)) {
						writeToFile(
								"***************************ERROR: User Story US10: Marriage after fourteen********************************\n"
										+ "Family ID: " + fam.getId()
										+ "  has a marriage before one or both spouses turn 14 "
										+ "\n**********************************************************************************************************\n");

					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	static void fewerThanFifteenSiblings(HashMap<String, Family> families) throws FileNotFoundException, IOException {
		// Sprint 2 Patrick Hill User Story US15 - Fewer Than 15 Siblings
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			if (fam.getChild() != null) {
				if (fam.getChild().size() > 15) {
					writeToFile(
							"***************************ERROR: User Story US15: Fewer Than Fifteen Children****************************\nFamily ID: "
									+ fam.getId()
									+ "  has more than 15 Children\nThe Number of children the family has is: "
									+ fam.getChild().size()
									+ "\n**********************************************************************************************************\n");
				}
			}
		}
	}

	static void noBigamy(HashMap<String, Family> families) throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 - Jason Sarwar - User Story US11 - No bigamy

	}

	static void multiplebirthslessthan5(HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 - Kuo Fan - User Story US14 - Multiple births less than 5
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		Date sib1;
		Date sib2;
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		int count = 0;
		while (famEntries.hasNext()) {
			HashMap<String, String> childMap = new HashMap<String, String>();
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			if (fam.getChild() != null && fam.getChild().size() > 1) {
				ArrayList<String> children = fam.getChild();
				for (int i = 0; i < children.size(); i++) {
					Individual ind = individuals.get(children.get(i));
					childMap.put(ind.getId(), ind.getBirth());
				}
				Iterator it = childMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					sib1 = sdf.parse(pair.getValue().toString());
					calendar1.setTime(sib1);
					Iterator it2 = childMap.entrySet().iterator();
					while (it2.hasNext()) {
						Map.Entry pair2 = (Map.Entry) it2.next();
						sib2 = sdf.parse(pair2.getValue().toString());
						calendar2.setTime(sib2);
						if ((calendar1 == calendar2) && pair.getKey() != pair2.getKey()) {
							count++;
						}
						if (count > 5) {
							writeToFile(
									"***************************ERROR: User Story US14: Multiple births less than 5****************************************\nFamily ID: "
											+ fam.getId() + "   More than five siblings born at the same time "
											+ "\n**********************************************************************************************************\n");
						}
					}

				}
				childMap = null;
			}
		}

	}

	static void Maillastname(HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 - Kuo Fan - User Story US16 - Old Male last names
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		String lastname;
		String male = null;
		String lastname1;
		String lastname2;
		if (entries.hasNext()) {
			HashMap<String, String> nameMap = new HashMap<String, String>();
			Map.Entry<String, Individual> indi = entries.next();
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			if (fam.getChild() != null && fam.getChild().size() > 1) {
				ArrayList<String> children = fam.getChild();
				for (int i = 0; i < children.size(); i++) {
					Individual ind = individuals.get(children.get(i));
					String fullname = ind.getName();
					male = ind.getSex();
					String[] words = fullname.split(" ");
					lastname = words[1];
					nameMap.put(male, lastname);
				}
				Iterator it = nameMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					lastname1 = pair.getValue().toString();
					Iterator it2 = nameMap.entrySet().iterator();
					while (it2.hasNext()) {
						Map.Entry pair2 = (Map.Entry) it2.next();
						lastname2 = pair2.getValue().toString();
						if (lastname1 != lastname2 && male == "male") {
							writeToFile(
									"***************************ERROR: User Story US16:Male last name ****************************************\nFamily ID: "
											+ fam.getId() + "   family numbers don't have same last name "
											+ "\n**********************************************************************************************************\n");
						}

					}

				}
				nameMap = null;
			}

		}
	}

	static void parentsNotTooOld(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 - Jason Sarwar - User Story US12 - Parents not too old
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> indFam = new HashMap<String, Family>(families);

		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		SimpleDateFormat ydf = new SimpleDateFormat("yyyy");

		Date birthDate = null;
		Date birthOfMom = null;
		Date birthOfDad = null;
		String strDate = "";
		String strMom = "";
		String strDad = "";

		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			if (ind.getChildOf() == null)
				continue;
			Family fam = indFam.get(ind.getChildOf());
			Individual dad = indMap.get(fam.getHusb());
			Individual mom = indMap.get(fam.getWife());
			try {
				birthDate = sdf.parse(ind.getBirth());
				strDate = ydf.format(birthDate);
				if (mom.getBirth() != null) {
					birthOfMom = sdf.parse(mom.getBirth());
					strMom = ydf.format(birthOfMom);
					if ((Integer.parseInt(strDate) - Integer.parseInt(strMom)) >= 60) {
						writeToFile(
								"***************************ERROR: User Story US12: Parents not too old****************************\nFamily ID: "
										+ fam.getId() + "\n" + "Mother is more than 60 years older than her child"
										+ "\n**********************************************************************************************************\n");
					}
				}
				if (dad.getBirth() != null) {
					birthOfDad = sdf.parse(dad.getBirth());
					strDad = ydf.format(birthOfDad);
					if ((Integer.parseInt(strDate) - Integer.parseInt(strDad)) >= 80) {
						writeToFile(
								"***************************ERROR: User Story US12: Parents not too old****************************\nFamily ID: "
										+ fam.getId() + "\n" + "Father is more than 80 years older than her child"
										+ "\n**********************************************************************************************************\n");
					}
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	static void siblingSpacing(HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 Patrick Hill User Story US13 - Sibling Spacing
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		Date sib1;
		Date sib2;
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		while (famEntries.hasNext()) {
			HashMap<String, String> childMap = new HashMap<String, String>();
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			if (fam.getChild() != null && fam.getChild().size() > 1) {
				ArrayList<String> children = fam.getChild();
				for (int i = 0; i < children.size(); i++) {
					Individual ind = individuals.get(children.get(i));
					childMap.put(ind.getId(), ind.getBirth());
				}
				Iterator it = childMap.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					sib1 = sdf.parse(pair.getValue().toString());
					calendar1.setTime(sib1);
					Iterator it2 = childMap.entrySet().iterator();
					while (it2.hasNext()) {
						Map.Entry pair2 = (Map.Entry) it2.next();
						sib2 = sdf.parse(pair2.getValue().toString());
						calendar2.setTime(sib2);
						if ((calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR))
								&& pair.getKey() != pair2.getKey()) {
							int diffMonth = Math.abs(calendar2.get(Calendar.MONTH) - calendar1.get(Calendar.MONTH));
							int diffDay = Math
									.abs(calendar2.get(Calendar.DAY_OF_MONTH) - calendar1.get(Calendar.DAY_OF_MONTH));
							if ((diffMonth < 8)) {
								Individual ind1 = individuals.get(pair.getKey());
								Individual ind2 = individuals.get(pair2.getKey());
								if (diffMonth == 0 && diffDay > 2) {
									writeToFile(
											"***************************ERROR: User Story US13: Sibling Spacing****************************************\nFamily ID: "
													+ fam.getId()
													+ "  has Children Born in the Same Month and Year and are More than 2 Days Apart\nIndividual 1: "
													+ ind1.getId() + " - " + ind1.getName() + " DOB: " + ind1.getBirth()
													+ "\nIndividual 2: " + ind2.getId() + " - " + ind2.getName()
													+ " DOB: " + ind2.getBirth()
													+ "\n**********************************************************************************************************\n");
								} else {
									writeToFile(
											"***************************ERROR: User Story US13: Sibling Spacing****************************************\nFamily ID: "
													+ fam.getId()
													+ "  has Children Born Less than Eight Months Apart\nIndividual 1: "
													+ ind1.getId() + " - " + ind1.getName() + " DOB: " + ind1.getBirth()
													+ "\nIndividual 2: " + ind2.getId() + " - " + ind2.getName()
													+ " DOB: " + ind2.getBirth()
													+ "\n**********************************************************************************************************\n");
								}
							}
						}
					}
				}
				childMap = null;
			}
		}
	}

	static void siblingsShouldNotMarry(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 3 - Jason Sarwar - User Story US18 - Siblings Should Not Marry
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);

		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();

		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			Individual dad = indMap.get(fam.getHusb());
			Individual mom = indMap.get(fam.getWife());
			if (dad.getChildOf() != null && mom.getChildOf() != null) {
				if (dad.getChildOf().equals(mom.getChildOf())) {
					writeToFile(
							"***********************ERROR: User Story US18: Siblings Should Not Marry***********************\n"
									+ dad.getId() + " - " + dad.getName() + " and " + mom.getId() + " - "
									+ mom.getName() + " are married and have the same parents"
									+ "\n**********************************************************************************************************\n");
				}
			}
		}
	}

	static void uniqueNameAndBirthdate(HashMap<String, Individual> individuals)
			throws FileNotFoundException, IOException {
		// Sprint 3 Patrick Hill User Story US23 - Unique Names and Birthdates
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			Map<String, Individual> indMap2 = new HashMap<String, Individual>(individuals);
			Iterator<Map.Entry<String, Individual>> indEntries2 = indMap2.entrySet().iterator();
			while (indEntries2.hasNext()) {
				Map.Entry<String, Individual> indEntry2 = indEntries2.next();
				Individual ind2 = indEntry2.getValue();
				if ((ind.getName().equals(ind2.getName())) && (ind.getBirth().equals(ind2.getBirth()))
						&& (ind.getId() != ind2.getId())) {
					writeToFile(
							"***************************ERROR: User Story US23: Unique Names and Birthdates****************************\nIndividual: "
									+ ind.getId() + " - " + ind.getName()
									+ "  has The same name and birthdate as Individual: " + ind2.getId() + " - "
									+ ind2.getName() + "\nThe DOB for both is: " + ind.getBirth()
									+ "\n**********************************************************************************************************\n");
				}
			}

		}
	}

	static void auntsAndUncles(HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 2 Patrick Hill User Story US20 - Aunts and Uncles
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		String fatherID = null;
		String motherID = null;
		String motherFamilyID = null;
		String fatherFamilyID = null;
		ArrayList<String> auntsUncles = new ArrayList<String>();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			fatherID = fam.getHusb();
			motherID = fam.getWife();
			Individual father = individuals.get(fatherID);
			Individual mother = individuals.get(motherID);
			motherFamilyID = mother.getChildOf();
			fatherFamilyID = father.getChildOf();
			if (motherFamilyID != null) {
				Family motherFamily = families.get(motherFamilyID);
				if (motherFamily.getChild() != null && motherFamily.getChild().size() > 1) {
					auntsUncles.addAll(motherFamily.getChild());
				}
			}
			if (fatherFamilyID != null) {
				Family fatherFamily = families.get(fatherFamilyID);
				if (fatherFamily.getChild() != null && fatherFamily.getChild().size() > 1) {
					auntsUncles.addAll(fatherFamily.getChild());
				}
			}
			if (fam.getChild() != null) {
				ArrayList<String> children = fam.getChild();
				for (int i = 0; i < children.size(); i++) {
					Individual ind = individuals.get(children.get(i));
					String spouseOf = ind.getSpouseOf();
					if (spouseOf != null) {
						Family childFam = families.get(spouseOf);
						String spouse = childFam.getHusb();
						if (spouse.equals(ind.getId())) {
							spouse = childFam.getWife();
						}
						if (auntsUncles.contains(spouse)) {
							Individual incest = individuals.get(spouse);
							writeToFile(
									"***************************ERROR: User Story US20: Aunts and Uncles***************************************\nIndividual: "
											+ ind.getId() + " - " + ind.getName()
											+ " is married to either their aunt or uncle " + incest.getId() + " - "
											+ incest.getName()
											+ "\n**********************************************************************************************************\n");
						}
					}
				}

			}
		}

	}

	static void uniqueFamiliesBySpouses(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		// Sprint 3 - Jason Sarwar - User Story US24 - Unique Families By
		// Spouses
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			Map<String, Family> famMap2 = new HashMap<String, Family>(families);
			Iterator<Map.Entry<String, Family>> famEntries2 = famMap2.entrySet().iterator();
			while (famEntries2.hasNext()) {
				Map.Entry<String, Family> famEntry2 = famEntries2.next();
				Family fam2 = famEntry2.getValue();
				if (fam.getHusb() != null && fam2.getHusb() != null && fam.getWife() != null
						&& fam2.getWife() != null) {
					if (fam.getHusb().equals(fam2.getHusb()) && fam.getWife().equals(fam2.getWife())
							&& fam.getMarriage().equals(fam2.getMarriage()) && fam.getId() != fam2.getId()) {
						writeToFile(
								"***************************ERROR: User Story US24: Unique Families By Spouses****************************\n"
										+ fam.getId() + " and " + fam2.getId()
										+ " have the same spouses and marriage dates"
										+ "\n**********************************************************************************************************\n");
					}
				}
			}
		}
	}

	static void uniqueID(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		if (!dupInd.isEmpty()) {
			for (int i = 0; i < dupInd.size(); i++) {
				Individual ind1 = dupInd.get(i);
				Individual ind2 = indMap.get(dupInd.get(i).getId());
				writeToFile(
						"***************************ERROR: User Story US22: Unique ID***************************************\nIndividual: "
								+ ind1.getId() + " - " + ind1.getName() + " has the same ID as " + ind2.getId() + " - "
								+ ind2.getName()
								+ "\n**********************************************************************************************************\n");

			}

		}
		if (!dupInd.isEmpty()) {
			for (int i = 0; i < dupFam.size(); i++) {
				Family fam1 = dupFam.get(i);
				Family fam2 = famMap.get(dupFam.get(i).getId());
				writeToFile(
						"***************************ERROR: User Story US22: Unique ID***************************************\nIndividual: "
								+ fam1.getId() + " has the same ID as " + fam2.getId()
								+ "\n**********************************************************************************************************\n");

			}
		}

	}

	static void cousinsNotMarry(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws FileNotFoundException, IOException {

		// Sprint 3 - Xuanhong Shen - User Story US19 - Cousins should not
		// marry

		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		ArrayList<String> husbGrandParent = new ArrayList<String>();
		while (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			Individual husb = indMap.get(fam.getHusb());
			Individual wife = indMap.get(fam.getWife());
			// find out the family id of husb's grandparents
			if (husb.getChildOf() != null) {
				Family husbFam = famMap.get(husb.getChildOf());
				Individual husbFather = indMap.get(husbFam.getHusb());
				Individual husbMother = indMap.get(husbFam.getWife());
				if (husbFather.getChildOf() != null) {
					husbGrandParent.add(husbFather.getChildOf());
				}
				if (husbMother.getChildOf() != null) {
					husbGrandParent.add(husbMother.getChildOf());
				}
			}
			if (wife.getChildOf() != null) {
				Family wifeFam = famMap.get(wife.getChildOf());
				Individual wifeFather = indMap.get(wifeFam.getHusb());
				Individual wifeMother = indMap.get(wifeFam.getWife());
				if (wifeFather.getChildOf() != null) {
					if (husbGrandParent.contains(wifeFather.getChildOf())) {
						writeToFile(
								"***************************ERROR: User Story US19: Cousins should not marry***************************************\nIndividual: "
										+ husb.getId() + " - " + husb.getName()
										+ " is married to his or her first cousin " + wife.getId() + " - "
										+ wife.getName()
										+ "\n**********************************************************************************************************\n");
					}
				}
				if (wifeMother.getChildOf() != null) {
					if (husbGrandParent.contains(wifeMother.getChildOf())) {
						writeToFile(
								"***************************ERROR: User Story US19: Cousins should not marry***************************************\nIndividual: "
										+ husb.getId() + " - " + husb.getName()
										+ " is married to his or her first cousin " + wife.getId() + " - "
										+ wife.getName()
										+ "\n**********************************************************************************************************\n");
					}
				}
			}

		}

	}

	static void currGenForRole(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 3 - Kuo Fan - User Story US21 Correct gender for role
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		if (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			Individual husb = indMap.get(fam.getHusb());
			Individual wife = indMap.get(fam.getWife());
			if (husb.getSex() != "male") {
				writeToFile(
						"***************************ERROR: User Story User Story US21 Correct gender for role ****************************************\nFamily ID: "
								+ husb.getId() + "  Husband is not male "
								+ "\n**********************************************************************************************************\n");

			} else if (wife.getSex() != "female") {
				writeToFile(
						"***************************ERROR: User Story User Story US21 Correct gender for role ****************************************\nFamily ID: "
								+ wife.getId() + "  wife is not famale "
								+ "\n**********************************************************************************************************\n");
			}

		}
	}

	static void listDeceased(HashMap<String, Individual> individuals) throws ParseException, FileNotFoundException, IOException{
		//Sprint 3 - Kuo Fan - User Story US29 - List Deceased
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		if (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			if (ind.getDeath() != null) {
				writeToFile(
						"***************************ERROR: User Story User Story US29 List all deceased individuals in a GEDCOM file ****************************************\nFamily ID: "
								+ ind.getId() + "   is dead "
								+ "\n**********************************************************************************************************\n");

			}
		}
	}
	static void listOrphans(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 4 - Kuo Fan - List all orphaned children (both parents dead and child < 18 years old) in a GEDCOM file
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		Date nowTime = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int age=0;
		if (indEntries.hasNext()) { 
			Date date_of_birth = null;
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
			if (famEntries.hasNext()){
				Map.Entry<String, Family> famEntry = famEntries.next();
				Family fam = famEntry.getValue();
				date_of_birth = sdf.parse(ind.getBirth());
				cal1.setTime(date_of_birth);
				cal2.setTime(nowTime);
				age = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
				Individual parent = indMap.get(ind.getChildOf());
			if(age<=18&&parent.getDeath()!=null){
				writeToFile(
						"***************************ERROR: User Story User Story US33 List all orphaned children (both parents dead and child < 18 years old) in a GEDCOM file ****************************************\nFamily ID: "
								+ ind.getId() + "   is orphan "
								+ "\n**********************************************************************************************************\n");
			}
				   }
				}
      }
	static void listLargeAgeDiff(HashMap<String, Individual> individuals, HashMap<String, Family> families)
			throws ParseException, FileNotFoundException, IOException {
		// Sprint 4 - Kuo Fan - List all couples who were married when the older spouse was more than twice as old as the younger spouse
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
		if (famEntries.hasNext()) {
			Map.Entry<String, Family> famEntry = famEntries.next();
			Family fam = famEntry.getValue();
			Individual husb = indMap.get(fam.getHusb());
			Individual wife = indMap.get(fam.getWife());
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			int differ=0;
			Date hus_birth=null;
			hus_birth=sdf.parse(husb.getBirth());
			Date wif_birth=null;
			wif_birth=sdf.parse(wife.getBirth());
			cal1.setTime(hus_birth);
			cal2.setTime(wif_birth);
			differ = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
			int differage=0;
			if (differ>0){
			differage=differ;
			if(differage>cal2.get(Calendar.YEAR)){
				writeToFile(
						"***************************ERROR: User Story User Story US33 List all orphaned children (both parents dead and child < 18 years old) in a GEDCOM file ****************************************\nFamily ID: "
								+ husb.getId() + "   is twice old than " + wife.getId()
								+ "\n**********************************************************************************************************\n");
			   }
			}
			else{
			differage=cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
			if(differage>cal1.get(Calendar.YEAR)){
				writeToFile(
						"***************************ERROR: User Story User Story US33 List all orphaned children (both parents dead and child < 18 years old) in a GEDCOM file ****************************************\nFamily ID: "
								+ wife.getId() + "   is twice old than " + husb.getId()
								+ "\n**********************************************************************************************************\n");
			 }
			}			
		 }
		}

	
	static void listLivingSingle(HashMap<String, Individual> individuals, HashMap<String, Family> families) throws ParseException, FileNotFoundException, IOException{
		// Sprint 4 - Jason Sarwar - User Story US31 - List Living Single
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		writeToFile("******User Story US31 - List of Living Single Individuals 30 years and older:\n");
		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
			int exists = 0;
			while (famEntries.hasNext()) {
				Map.Entry<String, Family> famEntry = famEntries.next();
				Family fam = famEntry.getValue();
				if(ind.getId().equals(fam.getHusb()) || ind.getId().equals(fam.getWife())) {
					exists = 1;
					break;
				}
					
			}
			
			Calendar cal = Calender.getInstance();
			Date now = sdf.format(cal.getTime());
			Date birthDate = sdf.parse(ind.getBirth());
			long diff = (now.getTime() - birthDate.getTime()) / (1000 * 60 * 60 * 24 * 365);
			
			if(!exists && diff >= 30) {
				writeToFile(ind.getName() + "\n");
			}
				
			
		}
		
		writeToFile("\n");
		
	}
	
	
	
	static void listRecentSurvivors(HashMap<String, Individual> individuals, HashMap<String, Family> families) throws ParseException, FileNotFoundException, IOException{
		// Sprint 4 - Jason Sarwar - User Story US37 - List Recent Survivors
		Map<String, Individual> indMap = new HashMap<String, Individual>(individuals);
		Map<String, Family> famMap = new HashMap<String, Family>(families);
		
		Iterator<Map.Entry<String, Individual>> indEntries = indMap.entrySet().iterator();
		writeToFile("******User Story US37 - List of Recent Survivors:\n");
		while (indEntries.hasNext()) {
			Map.Entry<String, Individual> indEntry = indEntries.next();
			Individual ind = indEntry.getValue();
			
			if(ind.getDeath() != null) {
			
				Calendar cal = Calender.getInstance();
				Date now = sdf.format(cal.getTime());
				Date deathDate = sdf.parse(ind.getDeath());
				long diff = (now.getTime() - deathDate.getTime()) / (1000 * 60 * 60 * 24);
				if(diff < 30) {
					Iterator<Map.Entry<String, Family>> famEntries = famMap.entrySet().iterator();
					while (famEntries.hasNext()) {
						Map.Entry<String, Family> famEntry = famEntries.next();
						Family fam = famEntry.getValue();
						if(ind.getId().equals(fam.getHusb())) {
							Individual wife = individuals.get(fam.getWife());
							writeToFile(wife.getName() + "\n");
							allDescendants(fam.getId(), families);
						}
						if(ind.getId().equals(fam.getWife())) {
							Individual husband = individuals.get(fam.getHusb());
							writeToFile(husband.getName() + "\n");
							allDescendants(fam.getId());
						}
											
												
					}
					
				}
				
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	static void allDescendants(String famId) throws ParseException, FileNotFoundException, IOException{
		// Helper function for US37 - Jason Sarwar
		Family fam = families.get(famId);
		ArrayList<String> listOfChildren = fam.getChild();
		if(listOfChildren != null) {
			for(int i = 0; i < listOfChildren.size(); i++) {
				Individual child = individuals.get(listOfChildren[i]);
				writeToFile(child.getName() + "\n");
				allDescendants(listOfChildren[i]);
			}
		}
	}
	
	
	static void upcomingBirthdays(HashMap<String,Individual> individuals) throws FileNotFoundException, IOException{
		//Sprint 4 US 38 Upcoming Birthdays  - Patrick Hill 
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		Date nowTime = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int diffDay = 0;
		int diffMonth = 0;
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			// Get the date of birth and date of death
			Date date_of_birth = null;
			try {
				date_of_birth = sdf.parse(indi.getBirth());
				cal1.setTime(date_of_birth);
				cal2.setTime(nowTime);
				diffDay = Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
				diffMonth = Math.abs(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Comparing the dates
			if (diffMonth == 0 && diffDay < 30) {
				writeToFile(
						"***************************ERROR: User Story US38: Upcoming Birthday**************************************\nIndividual: "
								+ indi.getId() + " - " + indi.getName()
								+ " has a birthday less than 30 days from today\nDOB: " + indi.getBirth()
								+ "\n**********************************************************************************************************\n");
			}
		}
	}

	static void upcomingAnniversaries(HashMap<String, Family> families) throws FileNotFoundException, IOException {
		// Sprint 4 US 39 Upcoming Anniversaries - Patrick Hill
		Map<String, Family> map = new HashMap<String, Family>(families);
		Iterator<Map.Entry<String, Family>> entries = map.entrySet().iterator();
		Date nowTime = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int diffDay = 0;
		int diffMonth = 0;
		while (entries.hasNext()) {
			Map.Entry<String, Family> entry = entries.next();
			Family fam = entry.getValue();
			// Get the date of birth and date of death
			Date date_of_marriage = null;
			try {
				date_of_marriage = sdf.parse(fam.getMarriage());
				cal1.setTime(date_of_marriage);
				cal2.setTime(nowTime);
				diffDay = Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
				diffMonth = Math.abs(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Comparing the dates
			if (diffMonth == 0 && diffDay < 30) {
				writeToFile(
						"***************************ERROR: User Story US39: Upcoming Anniversery**************************************\nFamily: "
								+ fam.getId() + " has an anniversery less than 30 days from today\nDate Of Marriage: "
								+ fam.getMarriage()
								+ "\n**********************************************************************************************************\n");
			}
		}

	}

	static void recentBirth(HashMap<String, Individual> individuals) throws FileNotFoundException, IOException {
		// Sprint 4 US 35 List Recent Birth - Xuanhong Shen
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		Date nowTime = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int diffDay = 0;
		int diffMonth = 0;
		int diffYear = 0;
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			// Get the date of birth and date of death
			Date date_of_birth = null;
			try {
				date_of_birth = sdf.parse(indi.getBirth());
				cal1.setTime(date_of_birth);
				cal2.setTime(nowTime);
				diffYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
				if (diffYear == 0) {
					diffDay = Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
					diffMonth = Math.abs(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Comparing the dates
			if (diffYear == 0 && diffMonth <= 1 && diffDay < 30) {
				writeToFile(
						"***************************ERROR: User Story US35: Recent Birth**************************************\nIndividual: "
								+ indi.getId() + " - " + indi.getName()
								+ " was born in the last 30 days from today\nDOB: " + indi.getBirth()
								+ "\n**********************************************************************************************************\n");
			}
		}
	}


	static void recentDeath(HashMap<String, Individual> individuals) throws FileNotFoundException, IOException {
		// Sprint 4 US 36 List Recent Death - Xuanhong Shen
		Map<String, Individual> map = new HashMap<String, Individual>(individuals);
		Iterator<Map.Entry<String, Individual>> entries = map.entrySet().iterator();
		Date nowTime = new Date(System.currentTimeMillis());
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		int diffDay = 0;
		int diffMonth = 0;
		int diffYear = 0;
		while (entries.hasNext()) {
			Map.Entry<String, Individual> entry = entries.next();
			Individual indi = entry.getValue();
			// Get the date of birth and date of death
			Date date_of_death = null;
			if (indi.getDeath() != null) {
				try {
					date_of_death = sdf.parse(indi.getDeath());
					cal1.setTime(date_of_death);
					cal2.setTime(nowTime);
					diffYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
					if (diffYear == 0) {
						diffDay = Math.abs(cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH));
						diffMonth = Math.abs(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Comparing the dates
				if (diffYear == 0 && diffMonth <= 1 && diffDay < 30) {
					writeToFile(
							"***************************ERROR: User Story US36: Recent Death**************************************\nIndividual: "
									+ indi.getId() + " - " + indi.getName()
									+ " was dead in the last 30 days from today\nDOD: " + indi.getDeath()
									+ "\n**********************************************************************************************************\n");
				}
			}

		}

	}
	
}