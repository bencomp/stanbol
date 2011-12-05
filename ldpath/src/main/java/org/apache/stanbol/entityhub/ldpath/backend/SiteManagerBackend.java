package org.apache.stanbol.entityhub.ldpath.backend;

import org.apache.stanbol.entityhub.core.mapping.ValueConverterFactory;
import org.apache.stanbol.entityhub.core.model.InMemoryValueFactory;
import org.apache.stanbol.entityhub.core.query.DefaultQueryFactory;
import org.apache.stanbol.entityhub.ldpath.backend.AbstractBackend;
import org.apache.stanbol.entityhub.servicesapi.EntityhubException;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.model.ValueFactory;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQueryFactory;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.apache.stanbol.entityhub.servicesapi.site.ReferencedSiteManager;

public class SiteManagerBackend extends AbstractBackend {

    protected final ReferencedSiteManager siteManager;
    private ValueFactory vf = InMemoryValueFactory.getInstance();
    private FieldQueryFactory qf = DefaultQueryFactory.getInstance();
    
    public SiteManagerBackend(ReferencedSiteManager siteManager) {
        this(siteManager,null);
    }
    public SiteManagerBackend(ReferencedSiteManager siteManager,ValueConverterFactory valueConverter) {
        super(valueConverter);
        if(siteManager == null){
            throw new IllegalArgumentException("The parsed ReferencedSiteManager MUST NOT be NULL");
        }
        this.siteManager = siteManager;
    }
    @Override
    protected FieldQuery createQuery() {
        return qf.createFieldQuery();
    }
    @Override
    protected Representation getRepresentation(String id) throws EntityhubException {
        return siteManager.getEntity(id).getRepresentation();
    }
    @Override
    protected ValueFactory getValueFactory() {
        return vf;
    }
    @Override
    protected QueryResultList<String> query(FieldQuery query) throws EntityhubException {
        return siteManager.findIds(query);
    }
    
}
