/*******************************************************************************
 * Copyright (c) 2003-2012 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation (and others) - Initial implementation, 2003-2004
 *     JBoss by Red Hat - Initial contribution, 2005 (was erroneously licensed under LGPL 2.1)
 */
package org.hibernate.eclipse.console.views.navigator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.progress.UIJob;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.views.navigator.PendingJob.NonConflictingRule;

public class ClearPlaceHolderJob extends UIJob {

	private AbstractTreeViewer viewer;
	private PendingNode placeHolder;
	private Object[] children;
	private Object parent;

	public ClearPlaceHolderJob(AbstractTreeViewer viewer, PendingNode placeHolder, Object parent, Object[] children) {
		super(HibernateConsoleMessages.ClearPlaceHolderJob_removing_place_holder);
		this.viewer = viewer;
		this.placeHolder = placeHolder;
		this.parent = parent;
		this.children = children;
		setRule(NonConflictingRule.INSTANCE);
	}

	public IStatus runInUIThread(IProgressMonitor monitor) {
		viewer.remove(placeHolder);
		viewer.add(parent, children);
		//viewer.update( children, null );
		return Status.OK_STATUS;
	}

}
