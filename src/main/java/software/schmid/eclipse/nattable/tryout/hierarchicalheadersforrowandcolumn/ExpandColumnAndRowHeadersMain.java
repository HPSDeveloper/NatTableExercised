package software.schmid.eclipse.nattable.tryout.hierarchicalheadersforrowandcolumn;

import java.util.AbstractMap;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ExpandColumnAndRowHeadersMain {
	
	public static void main(String[] args) {
		Shell shell = setupShell();
		
		boolean withScrollbars = args.length > 0 && args[0].equals("withScrollbars");
		new ExpandColumnAndRowHeadersTable().createTable(shell, withScrollbars);
		
		runShell(shell);
	}

	private static Shell setupShell() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(300, 130);
		shell.setText("Tree List Table with expand/collapse feature for Columns and Rows");
		shell.setLayout(new RowLayout());
		return shell;
	}

	private static void runShell(Shell shell) {
		shell.open();
		while (!shell.isDisposed()) {
			if(!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
		shell.getDisplay().dispose();
	}

}
