package co.bledo.gitmin.git;

public class CommitInfo {
	public String oldFile;
	public String newFile;
	public String oldContents;
	public String newContents;
	public String diff;
	public String getOldFile() {
		return oldFile;
	}
	public void setOldFile(String oldFile) {
		this.oldFile = oldFile;
	}
	public String getNewFile() {
		return newFile;
	}
	public void setNewFile(String newFile) {
		this.newFile = newFile;
	}
	public String getOldContents() {
		return oldContents;
	}
	public void setOldContents(String oldContents) {
		this.oldContents = oldContents;
	}
	public String getNewContents() {
		return newContents;
	}
	public void setNewContents(String newContents) {
		this.newContents = newContents;
	}
	public String getDiff() {
		return diff;
	}
	public void setDiff(String diff) {
		this.diff = diff;
	}
	
}
