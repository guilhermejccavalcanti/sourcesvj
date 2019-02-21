/*******************************************************************************
 * * Copyright 2011 Impetus Infotech.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 ******************************************************************************/
package com.impetus.client.cassandra.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.cassandra.thrift.IndexClause;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.impetus.client.cassandra.pelops.PelopsClient;
import com.impetus.kundera.client.Client;
import com.impetus.kundera.client.EnhanceEntity;
import com.impetus.kundera.metadata.MetadataUtils;
import com.impetus.kundera.metadata.model.EntityMetadata;
import com.impetus.kundera.persistence.AbstractEntityReader;
import com.impetus.kundera.persistence.EntityReader;
import com.impetus.kundera.query.exception.QueryHandlerException;

/**
 * @author vivek.mishra
 * 
 */
public class CassandraEntityReader extends AbstractEntityReader implements EntityReader
{
    List<IndexClause> conditions = new ArrayList<IndexClause>();

    private static Log log = LogFactory.getLog(CassandraEntityReader.class);

    public CassandraEntityReader(String luceneQuery)
    {
        this.luceneQueryFromJPAQuery = luceneQuery;
    }

    public CassandraEntityReader()
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.impetus.kundera.persistence.EntityReader#findById(java.lang.String,
     * com.impetus.kundera.metadata.model.EntityMetadata, java.util.List,
     * com.impetus.kundera.client.Client)
     */
    @Override
    public EnhanceEntity findById(String primaryKey, EntityMetadata m, List<String> relationNames, Client client)
    {
        try
        {
            return (EnhanceEntity) client.find(m.getEntityClazz(), m, primaryKey, relationNames);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method responsible for reading bacl entity and relations using secondary
     * indexes(if it holds any relation), else retrieve row keys using lucene.
     * 
     * @param m
     *            entity meta data
     * @param relationNames
     *            relation names
     * @param isParent
     *            if entity is not holding any relation.
     * @param client
     *            client instance
     * @return list of wrapped enhance entities.
     */

    @Override
    public List<EnhanceEntity> populateRelation(EntityMetadata m, List<String> relationNames, boolean isParent,
            Client client)
    {
        List<EnhanceEntity> ls = null;
        if (!isParent)
        {
            if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit()))
            {
                ls = ((PelopsClient) client).find(m, relationNames, this.conditions);
            }
            else
            {
                // prepare lucene query and find.
                Set<String> rSet = fetchDataFromLucene(client);

                try
                {
                    ls = (List<EnhanceEntity>) ((PelopsClient) client).find(m.getEntityClazz(), relationNames, true, m,
                            rSet.toArray(new String[] {}));
                }
                catch (Exception e)
                {
                    log.error("Error while executing handleAssociation for cassandra:" + e.getMessage());
                    throw new QueryHandlerException(e.getMessage());
                }
            }
        }
        else
        {
            if (MetadataUtils.useSecondryIndex(m.getPersistenceUnit()))
            {
                // in case need to search on secondry columns and it is not set
                // to true!
                ls = ((PelopsClient) client).find(this.conditions, m, true, null);
            }
            else
            {
                ls = onAssociationUsingLucene(m, client, ls);
            }
        }

        return ls;
    }

    /**
     * Method to set indexcluase conditions.
     * 
     * @param conditions
     *            index conditions.
     */
    public void setConditions(List<IndexClause> conditions)
    {
        this.conditions = conditions;
    }
}

