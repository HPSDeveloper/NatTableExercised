package software.schmid.eclipse.nattable.tryout.hierarchicalheadersforrowandcolumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.ExtendedReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeData;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeRowModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupExpandCollapseLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellPainterMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TreeList;

/**
 * This class exercises the creation of a Eclipse Nebula NatTable that has grouped columns and rows which may be collapsed and expanded.
 * Additional features demonstrated are:
 * 2023.03.15: Cell with image and text both constituting hyper separate links (Clicks on text currently only functional in header columns)
 */
public class ExpandColumnAndRowHeadersTable {
	Map<String,String> propertyToLabelMap = ImmutableMap.of("name", "Name", "population", "Population");
	String[] propertyNames = {"content.name", "content.population"};
	
	@PostConstruct
	public void createTable(Composite parent, boolean withScrollbars) {
		parent.setLayout(new GridLayout());
		
		IColumnPropertyAccessor<TreeItem<Area>> columnPropertyAccsssor = new ExtendedReflectiveColumnPropertyAccessor<>(propertyNames);
		EventList<TreeItem<Area>> eventList = GlazedLists.eventList(getSampleData());
		
		TreeList<TreeItem<Area>> treeList = new TreeList<TreeItem<Area>>(eventList, new TreeItemFormat(), TreeList.nodesStartExpanded());
		ListDataProvider<TreeItem<Area>> dataProvider = new ListDataProvider<>(treeList, columnPropertyAccsssor);
		
		DataLayer bodyDataLayer = new DataLayer(dataProvider);
		setColumnWidth(bodyDataLayer, withScrollbars);
		// Provides that all body cells are attached a label ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + <column_index>:
        bodyDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator() );
		
		final ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(bodyDataLayer, true);
		final ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		
		ColumnGroupModel columnGroupModel = new ColumnGroupModel();
		SelectionLayer selectionLayer = new SelectionLayer(new ColumnGroupExpandCollapseLayer(columnHideShowLayer, columnGroupModel));
		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());
		
		
		GlazedListTreeData<TreeItem<Area>> glazedListTreeData = new GlazedListTreeData<>(treeList);
		GlazedListTreeRowModel<TreeItem<Area>> glazedListTreeRowModel = new GlazedListTreeRowModel<>(glazedListTreeData);
		TreeLayer treeLayer = new TreeLayer(selectionLayer, glazedListTreeRowModel);
		treeLayer.setRegionName(GridRegion.BODY);
		ViewportLayer viewPortLayerBody = new ViewportLayer(treeLayer);
		
		
		/*
		 * The column headers:
		 */
		IDataProvider headerDataProvider = new DefaultColumnHeaderDataProvider (
				new ArrayList<>(propertyToLabelMap.keySet()).toArray(new String[0]),
				propertyToLabelMap);
		DataLayer headerDataLayer  = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewPortLayerBody, selectionLayer);
		// Provides that all header cells are attached a label ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + <column_index>:
		headerDataLayer.setConfigLabelAccumulator(new ColumnLabelAccumulator());
		
		ColumnGroupHeaderLayer columnGroupHeaderLayer = new ColumnGroupHeaderLayer(columnHeaderLayer, selectionLayer, columnGroupModel);
		int[] columnIndexes = {0,1};
		columnGroupHeaderLayer.addColumnsIndexesToGroup("Ueberschrift", columnIndexes);
		columnGroupHeaderLayer.setGroupUnbreakable(columnIndexes[0]);
		
		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnGroupHeaderLayer, 0,  0);
		compositeLayer.setChildLayer(GridRegion.BODY,  viewPortLayerBody, 0,  1);
		
		
		NatTable natTable = new NatTable(parent, SWT.V_SCROLL | SWT.H_SCROLL, compositeLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		
		// Prepare a text link and a icon link in one cell:
        final CellPainterDecorator textAndIconLinkDecorator = getPainterCombiningTextLinkAndIconLinkInOneCell();  
        
        // Register the cell with its text and icon as separate links within the NatTable:
        natTable.addConfiguration(createConfigForLinkedIconAndLinkedText(textAndIconLinkDecorator));
		
		natTable.configure();
		
		GridDataFactory.fillDefaults().grab(true,  true).applyTo(natTable);
		
	}


	/**
	 * Get a CellPainterDecorator that inserts a text and an icon into the cell
	 */
	private CellPainterDecorator getPainterCombiningTextLinkAndIconLinkInOneCell() {
		TextPainter textLink = new TextPainter();
        ImagePainter imageLink = new ImagePainter(new Image(Display.getDefault(),
        		ExpandColumnAndRowHeadersTable.class.getResourceAsStream("baseline_subway_black_48dp.png")));
        final CellPainterDecorator textAndIconLinkDecorator = 
                new CellPainterDecorator(textLink, CellEdgeEnum.RIGHT, imageLink);
		return textAndIconLinkDecorator;
	}


	/**
	 * Register the cell that contains a text and an icon painter with the NatTable. 
	 * The text and the icon shall trigger different link action
	 */
	private AbstractRegistryConfiguration createConfigForLinkedIconAndLinkedText(final CellPainterDecorator textWithIconSeparateLinksDecorator) {
		return new AbstractRegistryConfiguration() {
            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.CELL_PAINTER,
                        textWithIconSeparateLinksDecorator, DisplayMode.NORMAL,
                        ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 1);
            }

            @Override
            public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
                uiBindingRegistry.registerFirstSingleClickBinding(
                        new CellPainterMouseEventMatcher(
                                GridRegion.COLUMN_HEADER,
                                MouseEventMatcher.LEFT_BUTTON,
                                textWithIconSeparateLinksDecorator.getBaseCellPainter()),
                        new IMouseAction() {

							@Override
							public void run(NatTable arg0, MouseEvent arg1) {
								openMessageDialogue("Textfield clicked!!!");
							}
                        	
                        });

                uiBindingRegistry.registerFirstSingleClickBinding(
                        new CellPainterMouseEventMatcher(
                                GridRegion.COLUMN_HEADER,
                                MouseEventMatcher.LEFT_BUTTON,
                                textWithIconSeparateLinksDecorator.getDecoratorCellPainter()),
                        new IMouseAction() {

							@Override
							public void run(NatTable arg0, MouseEvent arg1) {
								openMessageDialogue("Icon clicked!!!");
							}
                        });
            }
        };
	}
	

	
	private void setColumnWidth(DataLayer bodyDataLayer, boolean withScrollbars) {
		if(withScrollbars) {
			bodyDataLayer.setColumnPercentageSizing(false);
			bodyDataLayer.setColumnWidthByPosition(0, 300);
			bodyDataLayer.setColumnWidthByPosition(1, 700);
		} else {
			bodyDataLayer.setColumnPercentageSizing(true);
			bodyDataLayer.setColumnWidthPercentageByPosition(0, 60);
			bodyDataLayer.setColumnWidthPercentageByPosition(1, 40);			
		}
	}


	private Collection getSampleData() {
		
		TreeItem<Area> globe = new TreeItem<>(new Area("Globe", 8000));
		
		TreeItem<Area> europe = new TreeItem<>(new Area("Europe", 983), globe);
		TreeItem<Area> switzerland = new TreeItem<>(new Area("Switzerland", 9), europe);
		TreeItem<Area> germany = new TreeItem<>(new Area("Germany", 85), europe);
		TreeItem<Area> russia = new TreeItem<>(new Area("Russia", 144), europe);
		
		TreeItem<Area> asia = new TreeItem<>(new Area("Asia", 3203), globe);
		TreeItem<Area> japan = new TreeItem<>(new Area("Japan", 203), asia);		
		TreeItem<Area> china = new TreeItem<>(new Area("China", 1230), asia);		
		TreeItem<Area> india = new TreeItem<>(new Area("India", 1130), asia);	
		
		return ImmutableList.of(globe, europe, switzerland, germany, russia, asia, japan, china, india);
	}
	
    private void openMessageDialogue(String text) {
    	MessageBox dialog =
    		    new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
    		dialog.setText("Link was clicked");
    		dialog.setMessage(text);

    		// open dialog and await user selection
    		dialog.open();
    }

}