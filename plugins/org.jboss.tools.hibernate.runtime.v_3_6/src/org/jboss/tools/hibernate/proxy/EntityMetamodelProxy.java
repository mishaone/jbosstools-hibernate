package org.jboss.tools.hibernate.proxy;

import org.hibernate.EntityMode;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;

public class EntityMetamodelProxy implements IEntityMetamodel {
	
	private EntityMetamodel target = null;

	public EntityMetamodelProxy(EntityMetamodel emm) {
		target = emm;
	}

	@Override
	public Integer getPropertyIndexOrNull(String id) {
		return target.getPropertyIndexOrNull(id);
	}

	@Override
	public Object getTuplizerPropertyValue(Object entity, int i) {
		return target.getTuplizer(EntityMode.POJO).getPropertyValue(entity, i);
	}

}
