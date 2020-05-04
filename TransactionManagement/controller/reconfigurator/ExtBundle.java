package reconfigurator;

public class ExtBundle {
	int bundleId;
	String bundleLocation;
	String state;
	String bundleName;
	String version;
	public ExtBundle() {
		
	}
	public ExtBundle(int bundleId, String bundleLocation, String state,
			String bundleName, String version) {
		super();
		this.bundleId = bundleId;
		this.bundleLocation = bundleLocation;
		this.state = state;
		this.bundleName = bundleName;
		this.version = version;
	}
	public int getBundleId() {
		return bundleId;
	}
	public void setBundleId(int bundleId) {
		this.bundleId = bundleId;
	}
	public String getBundleLocation() {
		return bundleLocation;
	}
	public void setBundleLocation(String bundleLocation) {
		this.bundleLocation = bundleLocation;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}