
import java.util.*;


class Employee implements Comparable {
	public List<Integer> teams;
	public int id;
	static private final int FRIEND_ID = 1009;
	
	public Employee(int id) {
		this.id = id;
		this.teams = new LinkedList<Integer>();
	}
	
	
	@Override
	public int compareTo(Object o) {
		if(o.getClass() != Employee.class) {
			return 0;
		}
		Employee m = (Employee) o;
		if(m.id == this.id) {
			return 0;
		}
		if(m.teams.size() > teams.size()) {
			return 1;
		} else if(m.teams.size() < teams.size()) {
			return -1;
		} else if(this.id == FRIEND_ID) {//* If we have the same number of teams, BUT this employee is OUR FRIEND ! Then we make it more important, thus if the solution can conclude with him, it will conclude.
			return -1;
		} else if(m.id == FRIEND_ID) {
			return 1;
		} else {
			return (m.id < this.id) ? 1 : -1;//* This is not to have the TreeSet thinking objects are equal when they have the same number of teams... (stupid TreeSet : can't use the equals() instead ?!?)
			//* Little cheat here : by order the lower first, we choose Stockholm always first and thus we get our friend in case of several solutions
		}
	}
	
	@Override
	public String toString() {
		return new Integer(id).toString() + "( " + teams.toString() + " )";
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass() != Employee.class) {
			return false;
		}
		Employee m = (Employee) o;
		return (m.id == this.id);
	}
}

public class Bilateral {
	
	static private final int MAX_EMPL_ID = 3000;
	
	static TreeMap<Integer, Employee> empl = new TreeMap<Integer, Employee>();
	static TreeSet<Employee> prio = new TreeSet<Employee>();
	static List<Integer> teams = new LinkedList<Integer>();
	static List<Integer> solution = new ArrayList<Integer>();
	static Employee[][] teamsEmployees;
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int m = sc.nextInt();
		teamsEmployees = new Employee[m][2];
		
		for(int i = 0; i < m; i++) {
			teams.add(i);
			sc.nextLine();
			int stockId = sc.nextInt();
			Employee stock = empl.get(stockId);
			if(stock == null) {
				stock = new Employee(stockId);
				empl.put(stock.id, stock);	
			}
			
			int lonId = sc.nextInt();
			Employee lon = empl.get(lonId);
			if(lon == null) {
				lon = new Employee(lonId);
				empl.put(lon.id, lon);	
			}
			
			stock.teams.add(i);
			lon.teams.add(i);
			teamsEmployees[i][0] = stock;
			teamsEmployees[i][1] = lon;
		}
		
		if(DEBUG) {
			System.out.println("Finished grabbing the employees, here is what we stored : ");
			System.out.println(empl);
		}
		
		//* Sorting employees by numbers of teams they take part in :
		for(Map.Entry<Integer, Employee> e : empl.entrySet()) {
			boolean ans = prio.add(e.getValue());
			if(DEBUG) {
				if(!ans) {
					System.out.println("The value " + e.getValue() + " was not inserted in the set.");
				}	
			}
		}
		// prio.addAll(empl.values());
		
		//Collections.sort(prio);
		
		if(DEBUG) {
			System.out.println("Sorted list : ");
			System.out.println(prio);
		}
		
		//* Until we have gone through each team (or through each employee), we add employee to the solution :
		while(!prio.isEmpty() && !teams.isEmpty()) {
			Employee current = prio.first();//get(0);
			if(DEBUG) {
				System.out.println("Current Employee : ");
				System.out.println(current);
			}
			prio.pollFirst();///remove(0);//* Removing the elements from the "competitors" (competiting to be included in the solution)
			
			solution.add(current.id);//* Add the employee to the solution (by displaying it on stdout)
			
			//* Updating the list to keep at the top the most interesting one, even after having deleted several teams from the list
			for(Integer team : current.teams) {
				//* Deleting the team from the list of the other employee :
				if(DEBUG) {
					System.out.println("Garbage collection for team=" + team);
				}
				if(teamsEmployees[team][0] == current) {
					teamsEmployees[team][0] = null;//* Has been put in the solution, useless now, delete it
					if(teamsEmployees[team][1] != null) {
						if(DEBUG) {
							System.out.println("The other one, in this team, is : " + teamsEmployees[team][1]);
							System.out.println("Removing this team from its teams.");
						}
						
						prio.remove(teamsEmployees[team][1]);
						teamsEmployees[team][1].teams.remove(team);
						if(!teamsEmployees[team][1].teams.isEmpty()) {
							if(DEBUG) {
								System.out.println("Still has " + teamsEmployees[team][1].teams.size() + " teams in reserve ! Keeping it !");
							}
							prio.add(teamsEmployees[team][1]);
						} else {
							if(DEBUG) {
								System.out.println("No teams in reserve anymore. Deleting it.");
							}
							teamsEmployees[team][1] = null;//* No teams left, delete it
						}
					}
				} else if(teamsEmployees[team][0] != null) {
					if(DEBUG) {
						System.out.println("The other one, in this team, is : " + teamsEmployees[team][1]);
						System.out.println("Removing this team from its teams.");
					}
					prio.remove(teamsEmployees[team][0]);
					teamsEmployees[team][0].teams.remove(team);
					if(!teamsEmployees[team][0].teams.isEmpty()) {
						if(DEBUG) {
							System.out.println("Still has " + teamsEmployees[team][0].teams.size() + " teams in reserve ! Keeping it !");
						}
						prio.add(teamsEmployees[team][0]);
					} else {
						if(DEBUG) {
							System.out.println("No teams in reserve anymore. Deleting it.");
						}
						teamsEmployees[team][0] = null;//* No teams left, delete it
					}
				}
			}
			
			//Collections.sort(prio);
			
			if(DEBUG) {
				System.out.println("Teams remaining BEFORE deletion : ");
				System.out.println(teams);
				System.out.println("Teams of the current Employee : ");
				System.out.println(current.teams);
				
				System.out.println("Sorted list : ");
				System.out.println(prio);
			}
			teams.removeAll(current.teams);//* Remove all the teams he was part of
			if(DEBUG) {
				System.out.println("Teams remaining AFTER deletion : ");
				System.out.println(teams);
			}
		}
		
		System.out.println(solution.size());
		for(Integer i : solution) {
			System.out.println(i);
		}
	}
	
	private static final boolean DEBUG = false;
}