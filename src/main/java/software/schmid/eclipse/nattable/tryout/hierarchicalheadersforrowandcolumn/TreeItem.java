package software.schmid.eclipse.nattable.tryout.hierarchicalheadersforrowandcolumn;

import java.util.ArrayList;
import java.util.List;

public class TreeItem<T> {
	private T content;
	private TreeItem<T> parent;
	private List<TreeItem<T>> children = new ArrayList<>();
	
	public TreeItem(T content, TreeItem<T> parent) {
		this.content=content;
		this.parent=parent;
		this.parent.addChild(this);
	}
	public TreeItem(T content) {
		this.content=content;
	}

	private void addChild(TreeItem<T> item) {
		children.add(item);
	}

	public T getContent() {
		return content;
	}
	
	public TreeItem<T> getParent() {
		return parent;
	}
	
	public boolean hasChildren() {
		return children.size() > 0;
	}
}
