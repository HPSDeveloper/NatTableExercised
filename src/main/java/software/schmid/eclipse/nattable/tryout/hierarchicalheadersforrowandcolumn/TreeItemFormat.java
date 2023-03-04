package software.schmid.eclipse.nattable.tryout.hierarchicalheadersforrowandcolumn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ca.odell.glazedlists.TreeList;

public class TreeItemFormat implements TreeList.Format<TreeItem<Area>>{

	@Override
	public boolean allowsChildren(TreeItem<Area> treeItem) {
		return treeItem.hasChildren();
	}

	@Override
	public Comparator<? super TreeItem<Area>> getComparator(int treeItem) {
		return (a,b) -> a.getContent().toString().compareTo(b.getContent().toString());
	}

	@Override
	public void getPath(List<TreeItem<Area>> path, TreeItem<Area> treeItem) {
		if(treeItem.getParent() != null) {
			List<TreeItem<Area>> parentPath = new ArrayList<>();
			getPath(parentPath, treeItem.getParent());
			path.addAll(parentPath);
		}
		path.add(treeItem);
	}

}
