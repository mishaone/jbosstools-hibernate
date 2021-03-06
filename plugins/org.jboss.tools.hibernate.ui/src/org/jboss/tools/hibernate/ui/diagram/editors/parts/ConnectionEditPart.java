/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.ui.diagram.editors.figures.RoundPolylineConnection;
import org.jboss.tools.hibernate.ui.diagram.editors.model.BaseElement;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection;

/**
 * @author some modifications from Vitali
 */
public class ConnectionEditPart extends AbstractConnectionEditPart 
	implements PropertyChangeListener, EditPartListener {
	
	public void activate() {
		if (!isActive()) {
			super.activate();
			((BaseElement) getModel()).addPropertyChangeListener(this);
			addEditPartListener(this);
		}
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ShapesSelectionEditPolicy());
	}
	
	protected IFigure createFigure() {
		PolylineConnection connection = new RoundPolylineConnection();		
		connection.setForegroundColor(getColor());	
		connection.setTargetDecoration(new PolygonDecoration());		
		connection.setVisible(getModelConnection().isVisible());
		return connection;
	}
	
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((BaseElement) getModel()).removePropertyChangeListener(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if (BaseElement.SELECTED.equals(property)) {
			updateSelected((Boolean)event.getNewValue());
		} else if (BaseElement.VISIBLE.equals(property)) {
			getFigure().setVisible(((Boolean)event.getNewValue()).booleanValue());
		} else if (BaseElement.REFRESH.equals(property)) {
			getFigure().setVisible(getModelConnection().isVisible());
			updateSelected(getModelConnection().isSelected());
		}
	}
	
	protected void updateSelected(boolean selected) {
		//setSelected(selected ? EditPart.SELECTED : EditPart.SELECTED_NONE);
		if (!selected) {
			setSelected(EditPart.SELECTED_NONE);
		}
		getFigure().setForegroundColor(selected ? getSelectionColor() : getColor());
	}
	
	private Connection getModelConnection() {
		return (Connection) getModel();
	}

	protected Object getTargetElement() {
		return getModelConnection().getTarget().getOrmElement();
	}

	private Color getColor() {
		Object el = getTargetElement();
		if ((el instanceof IPersistentClass && ((IPersistentClass)el).isInstanceOfRootClass()) || (el instanceof IPersistentClass && ((IPersistentClass)el).isInstanceOfSubclass())) { 
			return ResourceManager.getInstance().getColor(new RGB(210, 155, 100));
		} else if (el instanceof IColumn || el instanceof ITable || el instanceof IProperty) { 
			return ResourceManager.getInstance().getColor(new RGB(160, 160, 160));
		}
		return ResourceManager.getInstance().getColor(new RGB(255, 0, 0));
	}

	private Color getSelectionColor() {
		Object el = getTargetElement();
		if ((el instanceof IPersistentClass && ((IPersistentClass)el).isInstanceOfRootClass()) || (el instanceof IPersistentClass && ((IPersistentClass)el).isInstanceOfSubclass())) { 
			return ResourceManager.getInstance().getColor(new RGB(112, 161, 99));
		} else if (el instanceof IColumn || el instanceof ITable || (el instanceof IValue && ((IValue)el).isComponent())) { 
			return ResourceManager.getInstance().getColor(new RGB(66, 173, 247));
		}
		return ResourceManager.getInstance().getColor(new RGB(255, 0, 0));
	}
	
	private class ShapesSelectionEditPolicy extends SelectionEditPolicy {

		protected void hideSelection() {
			getModelConnection().setSelected(false);
		}

		protected void showSelection() {
			getModelConnection().setSelected(true);
		}
		
	}

	public void childAdded(EditPart child, int index) {
		
	}

	public void partActivated(EditPart editpart) {
		
	}

	public void partDeactivated(EditPart editpart) {
		
	}

	public void removingChild(EditPart child, int index) {
		
	}

	public void selectedStateChanged(EditPart editpart) {
		if (this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((GEFRootEditPart) getParent()).setToFront(this);
		}
	}
}