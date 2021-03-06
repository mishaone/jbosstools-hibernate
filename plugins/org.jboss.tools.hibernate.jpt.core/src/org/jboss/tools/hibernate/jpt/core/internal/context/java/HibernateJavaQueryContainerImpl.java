/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.common.core.resource.java.NestableAnnotation;
import org.eclipse.jpt.common.utility.internal.iterable.IterableTools;
import org.eclipse.jpt.common.utility.internal.iterable.SubListIterableWrapper;
import org.eclipse.jpt.common.utility.iterable.ListIterable;
import org.eclipse.jpt.jpa.core.context.Query;
import org.eclipse.jpt.jpa.core.context.java.JavaQueryContainer;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.java.GenericJavaQueryContainer;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateAbstractJpaFactory;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedNativeQuery;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernateNamedQuery;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedNativeQueryAnnotation;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.HibernateNamedQueryAnnotation;

/**
 *
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaQueryContainerImpl extends GenericJavaQueryContainer
implements HibernateJavaQueryContainer{

	
	protected final ContextListContainer<HibernateJavaNamedQuery, HibernateNamedQueryAnnotation> hibernateNamedQueryContainer;
	protected final ContextListContainer<HibernateJavaNamedNativeQuery, HibernateNamedNativeQueryAnnotation> hibernateNamedNativeQueryContainer;

	public HibernateJavaQueryContainerImpl(JavaQueryContainer.Parent owner) {
		super(owner);
		this.hibernateNamedQueryContainer = this.buildHibernateNamedQueryContainer();
		this.hibernateNamedNativeQueryContainer = this.buildHibernateNamedNativeQueryContainer();
	}

	@Override
	protected HibernateAbstractJpaFactory getJpaFactory() {
		return (HibernateAbstractJpaFactory) super.getJpaFactory();
	}


	// ********** synchronize/update **********

	@Override
	public void synchronizeWithResourceModel() {
		super.synchronizeWithResourceModel();
		this.syncHibernateNamedQueries();
		this.syncHibernateNamedNativeQueries();
	}

	@Override
	public void update() {
		super.update();
		this.updateModels(this.getHibernateNamedQueries());
		this.updateModels(this.getHibernateNamedNativeQueries());
	}
	
	// ********** queries **********

	@SuppressWarnings("unchecked")
	public Iterable<Query> getQueries() {
		return IterableTools.concatenate(super.getQueries(),
				this.getHibernateNamedQueries(), this.getHibernateNamedNativeQueries());
	}

	// ********** hibernateNamed queries **********
	@Override
	public ListIterable<HibernateJavaNamedQuery> getHibernateNamedQueries() {
		return this.hibernateNamedQueryContainer;
	}

	@Override
	public int getHibernateNamedQueriesSize() {
		return this.hibernateNamedQueryContainer.size();
	}

	public HibernateNamedQuery addHibernateNamedQuery() {
		return this.addHibernateNamedQuery(this.getNamedQueriesSize());
	}

	@Override
	public HibernateJavaNamedQuery addHibernateNamedQuery(int index) {
		HibernateNamedQueryAnnotation annotation = this.addHibernateNamedQueryAnnotation(index);
		return this.hibernateNamedQueryContainer.addContextElement(index, annotation);
	}

	protected HibernateNamedQueryAnnotation addHibernateNamedQueryAnnotation(int index) {
		return (HibernateNamedQueryAnnotation) this.parent.getResourceAnnotatedElement().addAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME);
	}

	@Override
	public void removeHibernateNamedQuery(HibernateNamedQuery hibernateNamedQuery) {
		this.removeHibernateNamedQuery(this.hibernateNamedQueryContainer.indexOf((HibernateJavaNamedQuery) hibernateNamedQuery));
	}

	@Override
	public void removeHibernateNamedQuery(int index) {
		this.parent.getResourceAnnotatedElement().removeAnnotation(index, HibernateNamedQueryAnnotation.ANNOTATION_NAME);
		this.hibernateNamedQueryContainer.remove(index);
	}

	@Override
	public void moveHibernateNamedQuery(int targetIndex, int sourceIndex) {
		this.parent.getResourceAnnotatedElement().moveAnnotation(targetIndex, sourceIndex, HibernateNamedQueryAnnotation.ANNOTATION_NAME);
		this.hibernateNamedQueryContainer.move(targetIndex, sourceIndex);
	}

	protected HibernateJavaNamedQuery buildHibernateNamedQuery(HibernateNamedQueryAnnotation hibernateNamedQueryAnnotation) {
		return this.getJpaFactory().buildHibernateJavaNamedQuery(this, hibernateNamedQueryAnnotation);
	}

	protected void syncHibernateNamedQueries() {
		this.hibernateNamedQueryContainer.synchronizeWithResourceModel();
	}

	protected ListIterable<HibernateNamedQueryAnnotation> getHibernateNamedQueryAnnotations() {
		return new SubListIterableWrapper<NestableAnnotation, HibernateNamedQueryAnnotation>(
				this.getNestableHibernateNamedQueryAnnotations_()
			);
	}

	protected ListIterable<NestableAnnotation> getNestableHibernateNamedQueryAnnotations_() {
		return this.parent.getResourceAnnotatedElement().getAnnotations(HibernateNamedQueryAnnotation.ANNOTATION_NAME);
	}
	
	protected ContextListContainer<HibernateJavaNamedQuery, HibernateNamedQueryAnnotation> buildHibernateNamedQueryContainer() {
		return this.buildSpecifiedContextListContainer(HIBERNATE_NAMED_QUERIES_LIST, new HibernateNamedQueryContainerAdapter());
	}

	/**
	 * hibernateNamed query container adapter
	 */
	protected class HibernateNamedQueryContainerAdapter
		extends AbstractContainerAdapter<HibernateJavaNamedQuery, HibernateNamedQueryAnnotation>
	{
		@Override
		public HibernateJavaNamedQuery buildContextElement(HibernateNamedQueryAnnotation resourceElement) {
			return HibernateJavaQueryContainerImpl.this.buildHibernateNamedQuery(resourceElement);
		}
		@Override
		public ListIterable<HibernateNamedQueryAnnotation> getResourceElements() {
			return HibernateJavaQueryContainerImpl.this.getHibernateNamedQueryAnnotations();
		}
		@Override
		public HibernateNamedQueryAnnotation extractResourceElement(HibernateJavaNamedQuery contextElement) {
			return contextElement.getQueryAnnotation();
		}
		
	}

	// ********** hibernateNamed native queries **********
	@Override
	public ListIterable<HibernateJavaNamedNativeQuery> getHibernateNamedNativeQueries() {
		return this.hibernateNamedNativeQueryContainer;
	}

	@Override
	public int getHibernateNamedNativeQueriesSize() {
		return this.hibernateNamedNativeQueryContainer.size();
	}

	public HibernateNamedNativeQuery addHibernateNamedNativeQuery() {
		return this.addHibernateNamedNativeQuery(this.getNamedQueriesSize());
	}

	@Override
	public HibernateJavaNamedNativeQuery addHibernateNamedNativeQuery(int index) {
		HibernateNamedNativeQueryAnnotation annotation = this.addHibernateNamedNativeQueryAnnotation(index);
		return this.hibernateNamedNativeQueryContainer.addContextElement(index, annotation);
	}

	protected HibernateNamedNativeQueryAnnotation addHibernateNamedNativeQueryAnnotation(int index) {
		return (HibernateNamedNativeQueryAnnotation) this.parent.getResourceAnnotatedElement().addAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME);
	}

	@Override
	public void removeHibernateNamedNativeQuery(HibernateNamedNativeQuery hibernateNamedNativeQuery) {
		this.removeHibernateNamedNativeQuery(this.hibernateNamedNativeQueryContainer.indexOf((HibernateJavaNamedNativeQuery) hibernateNamedNativeQuery));
	}

	@Override
	public void removeHibernateNamedNativeQuery(int index) {
		this.parent.getResourceAnnotatedElement().removeAnnotation(index, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME);
		this.hibernateNamedNativeQueryContainer.remove(index);
	}

	@Override
	public void moveHibernateNamedNativeQuery(int targetIndex, int sourceIndex) {
		this.parent.getResourceAnnotatedElement().moveAnnotation(targetIndex, sourceIndex, HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME);
		this.hibernateNamedNativeQueryContainer.move(targetIndex, sourceIndex);
	}

	protected HibernateJavaNamedNativeQuery buildHibernateNamedNativeQuery(HibernateNamedNativeQueryAnnotation hibernateNamedNativeQueryAnnotation) {
		return this.getJpaFactory().buildHibernateJavaNamedNativeQuery(this, hibernateNamedNativeQueryAnnotation);
	}

	protected void syncHibernateNamedNativeQueries() {
		this.hibernateNamedNativeQueryContainer.synchronizeWithResourceModel();
	}

	protected ListIterable<HibernateNamedNativeQueryAnnotation> getHibernateNamedNativeQueryAnnotations() {
		return new SubListIterableWrapper<NestableAnnotation, HibernateNamedNativeQueryAnnotation>(
				this.getNestableHibernateNamedNativeQueryAnnotations_()
			);
	}

	protected ListIterable<NestableAnnotation> getNestableHibernateNamedNativeQueryAnnotations_() {
		return this.parent.getResourceAnnotatedElement().getAnnotations(HibernateNamedNativeQueryAnnotation.ANNOTATION_NAME);
	}
	
	protected ContextListContainer<HibernateJavaNamedNativeQuery, HibernateNamedNativeQueryAnnotation> buildHibernateNamedNativeQueryContainer() {
		return this.buildSpecifiedContextListContainer(HIBERNATE_NAMED_NATIVE_QUERIES_LIST, new HibernateNamedNativeQueryContainerAdapter());
	}

	/**
	 * hibernateNamed native query container adapter
	 */
	protected class HibernateNamedNativeQueryContainerAdapter
		extends AbstractContainerAdapter<HibernateJavaNamedNativeQuery, HibernateNamedNativeQueryAnnotation>
	{
		@Override
		public HibernateJavaNamedNativeQuery buildContextElement(HibernateNamedNativeQueryAnnotation resourceElement) {
			return HibernateJavaQueryContainerImpl.this.buildHibernateNamedNativeQuery(resourceElement);
		}
		@Override
		public ListIterable<HibernateNamedNativeQueryAnnotation> getResourceElements() {
			return HibernateJavaQueryContainerImpl.this.getHibernateNamedNativeQueryAnnotations();
		}
		@Override
		public HibernateNamedNativeQueryAnnotation extractResourceElement(HibernateJavaNamedNativeQuery contextElement) {
			return contextElement.getQueryAnnotation();
		}
	}

}
