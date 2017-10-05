package ddc.support.util;

import java.nio.file.Path;

public class FilePair {
	public Path source = null;
	public Path target = null;

	public FilePair(Path source, Path target) {
		super();
		this.source = source;
		this.target = target;
	}
	
	@Override
	public String toString() {
		return "[" + source.toString() + "] > [" + target.toString() + "]";
	}
}
