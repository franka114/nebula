/****************************************************************************
* Copyright (c) 2005-2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 */
public class CTreeItem extends AbstractItem {

	private int checkCell;
	private int treeCell;
	private CTreeItem parentItem;
	private List items = new ArrayList();
	private boolean autoHeight;

	private CTreeItem(AbstractContainer container, CTreeItem parent, int style, int index) {
		super(container, style, index);
		initialize(parent, index);
	}
	public CTreeItem(CTree parent, int style) {
		this(parent, null, style, -1);
	}
	public CTreeItem(CTree parent, int style, int index) {
		this(parent, null, style, index);
	}
	public CTreeItem(CTreeItem parent, int style) {
		this(parent.container, parent, style, -1);
	}
	public CTreeItem(CTreeItem parent, int style, int index) {
		this(parent.container, parent, style, index);
	}

	void addItem(int index, AbstractItem item) {
		if(index < 0 || index > items.size()-1) {
			items.add(item);
		} else {
			items.add(index, item);
		}
		container.addedItems.add(item);
		redraw();
	}

//	void addItem(AbstractItem item) {
//		addItem(-1, item);
//	}

	/**
	 * Computes the size of each cell using the widthHint and heightHint with the same
	 * index as the cell.
	 */
	public int computeHeight() {
		int[] widths = container.getColumnWidths();
		int height = cells[0].computeSize(widths[0], -1).y;
//		titleHeight = ((CTreeCell) cells[0]).getSize().y;
		for(int i = 1; i < cells.length; i++) {
			height = Math.max(height, cells[i].computeSize(widths[i], -1).y);
//			titleHeight = Math.max(titleHeight, ((CTreeCell) cells[0]).getSize().y);
		}
		return height;
	}

	/**
	 * The Cells of a CTableTreeItem are considered contiguous and unified, therefore
	 * the contains method is overridden
	 */
	public boolean contains(Point pt) {
		return getBounds().contains(pt);
	}
	
	protected void createCell(int index, int style) {
		if(hasCell(index)) {
			cells[index] = new CTreeCell(this, style);
		}
	}
	
	protected int getCellStyle(int index) {
		if(hasCell(index)) {
			int style = container.internalGetColumn(index).getStyle();
			return style;
		}
		return 0;
	}
	
	public void dispose() {
		if((parentItem != null) && (!parentItem.isDisposed())) parentItem.removeItem(this);
		
		List l = new ArrayList(items);
		for(Iterator i = l.iterator(); i.hasNext(); ) {
			((CTreeItem) i.next()).dispose();
		}
		
		super.dispose();
	}
	
	public boolean getAutoHeight() {
		return autoHeight;
	}
	
	public Rectangle getBounds() {
		int[] order = container.getColumnOrder();
		Rectangle r1 = cells[order[0]].bounds;
		Rectangle r2 = cells[order[order.length-1]].bounds;
		return new Rectangle(
				r1.x,
				r1.y,
				r2.x+r2.width-r1.x,
				r1.height
				);
	}
	
//	protected AbstractCell[] createCells(Class[] cellClasses) {
//		return super.createCells(cellClasses);
//	}
	
	public CTreeCell getCheckCell() {
		return hasCheckCell() ? (CTreeCell) cells[checkCell] : null;
	}

	public CTree getCTree() {
		return (CTree) container;
	}
	
	public CTreeCell getCTreeCell(int column) {
		return (CTreeCell) getCell(column);
	}
	
	/**
	 * Returns the Tree Cell expansion state
	 * <p>If there is no Tree Cell, simply returns false</p>
	 * @return
	 * @see org.aspencloud.widgets.ccontainer#getExpanded(boolean)
	 */
	public boolean getExpanded() {
		return (hasTreeCell()) ? getTreeCell().isOpen() : false;
	}
	
	/**
	 * If this item has a tree column, this method will return the first image from that column
	 * @return the image from the tree column, null if neither exist
	 */
	public Image getImage() {
		if(hasTreeCell()) {
			return getTreeCell().getImage();
		}
		return null;
	}
	
	/**
	 * @param column the column from which to get the image
	 * @return the first image from the given column
	 */
	public Image getImage(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getImage();
		}
		return null;
	}

	/**
	 * @param column the column from which to get the images
	 * @return the images from the given column
	 */
	public Image[] getImages(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getImages();
		}
		return new Image[0];
	}

//	protected int getFirstPaintedCellIndex() {
//		return container.getFirstPaintedColumnIndex();
//	}
	
	CTreeItem getItem(boolean up) {
		if(hasParentItem()) {
			CTreeItem parent = getParentItem();
			int ix = parent.indexOf(this);
			if(up) {
				if(ix == 0) return parent;
				return parent.getItem(ix - 1);
			} else {
				if(ix > parent.getItemCount() - 1) return parent.getItem(false);
				return parent.getItem(ix + 1);
			}
		} else {
			CTree parent = getParent();
			int ix = parent.indexOf(this);
			if(up) {
				if(ix == 0) return null;
				return parent.getItem(ix - 1);
			} else {
				if(ix > parent.getItemCount() - 1) return null;
				return parent.getItem(ix + 1);
			}
		}
	}
	
	public CTreeItem getItem(int index) {
		if((index >= 0) && (index < items.size())) {
			return (CTreeItem) items.get(index);
		}
		return null;
	}

//	int getIndex() {
//		if(hasParentItem()) return getParentItem().indexOf(this);
//		return getParent().indexOf(this);
//	}
	
	public int getItemCount() {
		return items.size();
	}
	
	public CTreeItem[] getItems() {
		if(items == null) return new CTreeItem[0];
		return (CTreeItem[]) items.toArray(new CTreeItem[items.size()]);
	}
	
	public CTree getParent() {
		return getCTree();
	}

	public int getParentIndent() {
		return hasParentItem() ? parentItem.getTreeIndent() : 0;
	}
	
	public CTreeItem getParentItem() {
		return parentItem;
	}

//	protected int getLastPaintedCellIndex() {
//		return container.getLastPaintedColumnIndex();
//	}
	
	public Point getSize() {
		Rectangle r1 = cells[0].bounds;
		Rectangle r2 = cells[cells.length-1].bounds;
		return new Point(
				r2.x+r2.width-r1.x,
				r2.height
				);
	}

	/**
	 * If this item has a tree column, this method will return the first image from that column
	 * @return the image from the tree column, null if neither exist
	 */
	public String getText() {
		if(hasTreeCell()) {
			return getTreeCell().getText();
		}
		return null;
	}

	/**
	 * @param column the column from which to get the image
	 * @return the first image from the given column
	 */
	public String getText(int column) {
		if(column >= 0 && column < cells.length) {
			return ((CTreeCell) cells[column]).getText();
		}
		return null;
	}
	
	public CTreeCell getTreeCell() {
		return hasTreeCell() ? (CTreeCell) cells[treeCell] : null;
	}

	public int getTreeIndent() {
		if(hasTreeCell()) {
			return getTreeCell().getIndent();
		}
		return 0;
	}
	
	public Rectangle getTreeToggleBounds() {
		if(hasTreeCell() && getTreeCell().getToggleVisible()) {
			return getTreeCell().getToggleBounds();
		}
		return null;
	}
	
	public boolean hasCheckCell() {
		return (checkCell >= 0 && checkCell < cells.length);
	}
	
	public boolean hasItems() {
		return !items.isEmpty();
	}

	public boolean hasParentItem() {
		return parentItem != null;
	}
	
	public boolean hasTreeCell() {
		return (treeCell >= 0 && treeCell < cells.length);
	}
	
	public int indexOf(CTreeItem item) {
		return items.indexOf(item);
	}

	private void initialize(Object parent, int index) {
		CTree ct = (CTree) container;
		treeCell = ct.getTreeColumn();
		if(parent != null) {
			checkCell = ct.getCheckColumn();
			parentItem = (CTreeItem) parent;
			((CTreeItem) parent).addItem(index, this);
		} else {
			checkCell = ct.getCheckRoots() ? ct.getCheckColumn() : -1;
			ct.addItem(index, this);
		}
	}
	
	public boolean isTreeTogglePoint(Point pt) {
		Rectangle r = getTreeToggleBounds();
		if(r != null) {
			return r.contains(pt);
		}
		return false;
	}
	
	/**
	 * Returns true if the receiver is visible and all parents up to and including the 
	 * root of its container are visible. Otherwise, false is returned.
	 * @return true if visible, false otherwise
	 * @see AbstractItem#getVisible()
	 * @see Control#isVisible()
	 */
	public boolean isVisible() {
		return ((CTree) container).isVisible(this);
	}

	public void redraw() {
		container.redraw(this);
	}
	
	void removeItem(CTreeItem item) {
		items.remove(item);
		if(!container.removedItems.contains(item)) {
			container.removedItems.add(item);
			boolean selChange = container.selection.remove(item);
			if(selChange) container.fireSelectionEvent(false);
			redraw();
		}
		if(items.isEmpty() && hasTreeCell()) {
			getTreeCell().setToggleVisible(false);
		}
	}

	void setBounds(int x, int y, int width, int height) {
		for(int i = 0; i < cells.length; i++) {
			// TODO: container.internalGetColumn(i).getBounds() is too expensive!
			if(x>-1 || width>-1) {
				if(container.internalTable != null) {
					AbstractColumn column = container.internalGetColumn(i);
					if(x>-1) cells[i].bounds.x = column.getLeft();
					if(width>-1) cells[i].bounds.width = column.getWidth();
				} else {
					if(x>-1) cells[i].bounds.x = container.getClientArea().x;
					if(width>-1) cells[i].bounds.width = container.getClientArea().width;
				}
			}
			if(y>-1) cells[i].bounds.y = y;
			if(height>-1) cells[i].bounds.height = height;
		}
	}
	
//	void setWidth(int column, int width) {
//		if(hasCell(column)) {
//			getCTreeCell(column).getsneedsLayout = true;
//		}
//	}
	
//	public void setBounds(Rectangle[] bounds) {
//		int i = 0, j = 0;
//		for( ; i < cells.length && j < bounds.length; i++) {
//			int span = cells[i].colSpan;
//			if(span > 1) {
//				Rectangle ub = new Rectangle(bounds[j].x,bounds[j].y,bounds[j].width,bounds[j].height);
//				for(j++; j < (i + span) && j < bounds.length; j++) {
//					ub.add(new Rectangle(bounds[j].x,bounds[j].y,bounds[j].width,bounds[j].height));
//				}
//				cells[i].setBounds(ub.x, ub.y, ub.width, ub.height);
//			} else {
//				cells[i].setBounds(bounds[j].x, bounds[j].y, bounds[j].width, bounds[j].height);
//				j++;
//			}
//		}
//		if(i < cells.length) {
//			j = bounds.length-1;
//			for( ; i < cells.length; i++) {
//				cells[i].setBounds(bounds[j].x,bounds[j].y,0,0);
//			}
//		}
//	}

	/**
	 * Sets the Tree Expansion state of the Item
	 * <p>Does not affect the expansion state of individual expandable cells</p>
	 * @param expanded
	 */
	public void setExpanded(boolean expanded) {
		if(hasTreeCell() && getTreeCell().isOpen() != expanded) {
			getTreeCell().setOpen(expanded);
		}
	}

	public void setImage(Image image) {
		if(hasTreeCell()) {
			getTreeCell().setImage(image);
		}
	}
	
	public void setImage(int column, Image image) {
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setImage(image);
		}
	}

	public void setImages(Image[] images) {
		AbstractCell[] cells = getCells();
		if(cells.length >= images.length) {
			for(int i = 0; i < images.length; i++) {
				((CTreeCell) cells[i]).setImage(images[i]);
			}
		}
	}

	public void setImages(int column, Image[] images) {
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setImages(images);
		}
	}

	public void setText(int column, String string) {
		AbstractCell[] cells = getCells();
		if((column >= 0) && (column < cells.length)) {
			((CTreeCell) cells[column]).setText(string);
		}
	}
	
	public void setText(String string) {
		AbstractCell[] cells = getCells();
		if(cells.length > 0) {
			((CTreeCell) cells[0]).setText(string);
		}
	}
	
	public void setText(String[] strings) {
		AbstractCell[] cells = getCells();
		if(cells.length >= strings.length) {
			for(int i = 0; i < strings.length; i++) {
				((CTreeCell) cells[i]).setText(strings[i]);
			}
		}
	}
	
	public void setTreeIndent(int indent) {
		if(hasTreeCell()) {
			getTreeCell().setIndent(indent);
		}
	}	
}