package org.apache.stanbol.entityhub.web.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.stanbol.entityhub.servicesapi.model.Entity;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.web.ModelWriter;
import org.apache.stanbol.entityhub.web.ModelWriterRegistry;
import org.codehaus.jettison.json.JSONException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true)
@Service(Object.class)
@Property(name="javax.ws.rs", boolValue=true)
@Provider
public class EntityWriter implements MessageBodyWriter<Entity> {

    private final Logger log = LoggerFactory.getLogger(EntityWriter.class);
    
    
    @Reference
    protected ModelWriterRegistry writerRegistry;
    
    @Override
    public long getSize(Entity t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if(Entity.class.isAssignableFrom(type)){
            if(mediaType.isWildcardType() && mediaType.isWildcardSubtype()){
                mediaType = ModelWriter.DEFAULT_MEDIA_TYPE;
            }
            return writerRegistry.isWriteable(getMatchType(mediaType), null);
        } else {
            return false;
        }
    }

    @Override
    public void writeTo(Entity entity,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String,Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        //check for wildcard
        if(mediaType.isWildcardType() && mediaType.isWildcardSubtype()){
            mediaType = ModelWriter.DEFAULT_MEDIA_TYPE;
        }
        String charset = mediaType.getParameters().get("charset");
        if(charset == null){
            charset = ModelWriter.DEFAULT_CHARSET;
            mediaType = mediaType.withCharset(ModelWriter.DEFAULT_CHARSET);
            httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        }
        Iterator<ServiceReference> refs = writerRegistry.getModelWriters(
            getMatchType(mediaType), entity.getRepresentation().getClass()).iterator();
        ModelWriter writer = null;
        MediaType selectedMediaType = null;
        while((writer == null || selectedMediaType == null) && refs.hasNext()){
            writer = writerRegistry.getService(refs.next());
            if(writer != null){
                if(mediaType.isWildcardType() || mediaType.isWildcardSubtype()){
                    selectedMediaType = writer.getBestMediaType(mediaType);
                } else {
                    selectedMediaType = mediaType;
                }
            }
        }
        selectedMediaType = selectedMediaType.withCharset(charset);
        httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        if(writer == null || selectedMediaType == null){
            throw new WebApplicationException("Unable to serialize "+entity.getClass().getName()+" to "+mediaType);
        }
        log.debug("serialize Entity with {} data with ModelWriter {}",entity.getRepresentation().getClass().getName(), writer.getClass().getName());
        writer.write(entity, entityStream, selectedMediaType);
    }

    /**
     * Strips all parameters from the parsed mediaType
     * @param mediaType
     */
    protected MediaType getMatchType(MediaType mediaType) {
        final MediaType matchType;
        if(!mediaType.getParameters().isEmpty()){
            matchType = new MediaType(mediaType.getType(), mediaType.getSubtype());
        } else {
            matchType = mediaType;
        }
        return matchType;
    }
}