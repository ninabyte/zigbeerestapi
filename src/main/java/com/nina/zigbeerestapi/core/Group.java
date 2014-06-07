public class Group {
	private long id;
	private String groupAddress;
	private String name;
	private boolean on;
    private long onLastUpdated = 0L;

	public Group(long id) {
		this(id, "Group " + id);
	}
	public Group(long id, String name) {
		this.id = id;
		this.name = name;
	}

}