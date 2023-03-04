package software.schmid.eclipse.nattable.tryout.hierarchicalheadersforrowandcolumn;

public class Area {
	private String name;
	private long population;
	
	public Area(String name, long population) {
		super();
		this.name = name;
		this.population = population;
	}

	public String getName() {
		return name;
	}
	

	public long getPopulation() {
		return population;
	}

	@Override
	public String toString() {
		return "Area [name=" + name + ", population=" + population + "]";
	}
	
}
