//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2005 The OpenNMS Group, Inc. All rights
// reserved.
// OpenNMS(R) is a derivative work, containing both original code, included
// code and modified
// code that was published under the GNU General Public License. Copyrights
// for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2007 Apr 10: Cleanup imports. - dj@opennms.org
//
// Original code base Copyright (C) 1999-2001 Oculan Corp. All rights
// reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//    
// For more information contact:
// OpenNMS Licensing <license@opennms.org>
// http://www.opennms.org/
// http://www.opennms.com/
//

package org.opennms.netmgt.dao.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsCategory;
import org.opennms.netmgt.model.OnmsDistPoller;
import org.opennms.netmgt.model.OnmsIpInterface;
import org.opennms.netmgt.model.OnmsNode;
import org.opennms.netmgt.model.OnmsSnmpInterface;
import org.opennms.netmgt.model.SurveillanceStatus;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

/**
 * <p>NodeDaoHibernate class.</p>
 *
 * @author Ted Kazmark
 * @author David Hustace
 * @version $Id: $
 */
public class NodeDaoHibernate extends AbstractDaoHibernate<OnmsNode, Integer>
        implements NodeDao {

    /**
     * <p>Constructor for NodeDaoHibernate.</p>
     */
    public NodeDaoHibernate() {
        super(OnmsNode.class);
    }

    /** {@inheritDoc} */
    public OnmsNode get(String lookupCriteria) {
        if (lookupCriteria.contains(":")) {
            String[] criteria = lookupCriteria.split(":");
            return findByForeignId(criteria[0], criteria[1]);
        }
        return get(Integer.parseInt(lookupCriteria));
    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findNodes(final OnmsDistPoller distPoller) {
        return find("from OnmsNode where distPoller = ?", distPoller);
    }

    /** {@inheritDoc} */
    public OnmsNode getHierarchy(Integer id) {
        OnmsNode node = findUnique(
                          "select distinct n from OnmsNode as n "
                                  + "left join fetch n.assetRecord "
                                  + "where n.id = ?", id);
        
        initialize(node.getIpInterfaces());
        for (OnmsIpInterface i : node.getIpInterfaces()) {
            initialize(i.getMonitoredServices());
        }
        
        initialize(node.getSnmpInterfaces());
        for (OnmsSnmpInterface i : node.getSnmpInterfaces()) {
            initialize(i.getIpInterfaces());
        }
        
        return node;

    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findByLabel(String label) {
        return find("from OnmsNode as n where n.label = ?", label);
    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findAllByVarCharAssetColumn(
            String columnName, String columnValue) {
        return find("from OnmsNode as n where n.assetRecord." + columnName
                + " = ?", columnValue);
    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findAllByVarCharAssetColumnCategoryList(
            String columnName, String columnValue,
            Collection<OnmsCategory> categories) {
    	
        return find("select distinct n from OnmsNode as n "
        		+ "join n.categories as c "
                + "left join fetch n.assetRecord "
                + "left join fetch n.ipInterfaces as ipInterface "
                + "left join fetch ipInterface.monitoredServices as monSvc "
                + "left join fetch monSvc.serviceType "
                + "left join fetch monSvc.currentOutages "
                + "where n.assetRecord." + columnName + " = ? "
                + "and c.name in ("+categoryListToNameList(categories)+")", columnValue);
    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findByCategory(OnmsCategory category) {
        return find("select distinct n from OnmsNode as n "
                    + "join n.categories c "
                    + "left join fetch n.assetRecord "
                    + "left join fetch n.ipInterfaces as ipInterface "
                    + "left join fetch ipInterface.monitoredServices as monSvc "
                    + "left join fetch monSvc.serviceType "
                    + "left join fetch monSvc.currentOutages "
                    + "where c.name = ?",
                    category.getName());
    }

	private String categoryListToNameList(Collection<OnmsCategory> categories) {
		List<String> categoryNames = new ArrayList<String>();
    	for (OnmsCategory category : categories) {
			categoryNames.add(category.getName());
		}
		return StringUtils.collectionToDelimitedString(categoryNames, ", ", "'", "'");
	}
        
        

    /** {@inheritDoc} */
    public Collection<OnmsNode> findAllByCategoryList(
            Collection<OnmsCategory> categories) {
        return find("select distinct n from OnmsNode as n "
                + "join n.categories c " 
                + "left join fetch n.assetRecord "
                + "left join fetch n.ipInterfaces as ipInterface "
                + "left join fetch n.snmpInterfaces as snmpIface"
                + "left join fetch ipInterface.monitoredServices as monSvc "
                + "left join fetch monSvc.serviceType "
                + "left join fetch monSvc.currentOutages "
                + "where c.name in ("+categoryListToNameList(categories)+")"
                + "and n.type != 'D'");
    }

    /** {@inheritDoc} */
    public Collection<OnmsNode> findAllByCategoryLists( final Collection<OnmsCategory> rowCategories, final Collection<OnmsCategory> columnCategories) {
    	
        return getHibernateTemplate().execute(new HibernateCallback<Collection<OnmsNode>>() {

            public Collection<OnmsNode> doInHibernate(Session session) throws HibernateException, SQLException {
                
                return (Collection<OnmsNode>)session.createQuery("select distinct n from OnmsNode as n "
                + "join n.categories c1 "
                + "join n.categories c2 "
                + "left join fetch n.assetRecord "
                + "left join fetch n.ipInterfaces as iface "
                + "left join fetch n.snmpInterfaces as snmpIface"
                + "left join fetch iface.monitoredServices as monSvc "
                + "left join fetch monSvc.serviceType "
                + "left join fetch monSvc.currentOutages "
                + "where c1 in (:rowCategories) "
                + "and c2 in (:colCategories) "
                + "and n.type != 'D'")
                .setParameterList("rowCategories", rowCategories)
                .setParameterList("colCategories", columnCategories)
                .list();
                

            }

        });
        
    }
    
    public static class SimpleSurveillanceStatus implements SurveillanceStatus {
        
        private int m_serviceOutages;
        private int m_upNodeCount;
        private int m_nodeCount;
        
        public SimpleSurveillanceStatus(Number serviceOutages, Number upNodeCount, Number nodeCount) {
            System.err.println(String.format("Args: %s (%s), %s (%s), %s (%s)", 
                    serviceOutages, serviceOutages == null ? null : serviceOutages.getClass(),
                    upNodeCount, upNodeCount == null ? null : upNodeCount.getClass(),
                    nodeCount, nodeCount == null ? null : nodeCount.getClass()
                    ));
                    
            m_serviceOutages = serviceOutages == null ? 0 : serviceOutages.intValue();
            m_upNodeCount = upNodeCount == null ? 0 : upNodeCount.intValue();
            m_nodeCount = nodeCount == null ? 0 : nodeCount.intValue();
        }

        public Integer getDownEntityCount() {
            return m_nodeCount - m_upNodeCount;
        }

        public Integer getTotalEntityCount() {
            return m_nodeCount;
        }

        public String getStatus() {
            switch (m_serviceOutages) {
            case 0:  return "Normal";
            case 1:  return "Warning";
            default: return "Critical";
            }
        }
        
    }
    public SurveillanceStatus findSurveillanceStatusByCategoryLists(final Collection<OnmsCategory> rowCategories, final Collection<OnmsCategory> columnCategories) {
        return getHibernateTemplate().execute(new HibernateCallback<SurveillanceStatus>() {

            public SurveillanceStatus doInHibernate(Session session) throws HibernateException, SQLException {
                return (SimpleSurveillanceStatus)session.createSQLQuery("select" +
                		" count(distinct case when outages.outageid is not null and monSvc.status = 'A' then monSvc.id else null end) as svcCount," +
                		" count(distinct case when outages.outageid is null and monSvc.status = 'A' then node.nodeid else null end) as upNodeCount," +
                		" count(distinct node.nodeid) as nodeCount" +
                		" from node" +
                		" join category_node cn1 using (nodeid)" +
                		" join category_node cn2 using (nodeid)" +
                		" left outer join ipinterface ip using (nodeid)" +
                		" left outer join ifservices monsvc on (monsvc.ipinterfaceid = ip.id)" +
                		" left outer join outages on (outages.ifserviceid = monsvc.id and outages.ifregainedservice is null)" +
                        " where nodeType <> 'D'" +
                		" and cn1.categoryid in (:rowCategories)" +
                		" and cn2.categoryid in (:columnCategories)"
                		)
                		.setParameterList("rowCategories", rowCategories)
                		.setParameterList("columnCategories", columnCategories)
                		.setResultTransformer(new ResultTransformer() {
                            private static final long serialVersionUID = 1L;

                            public Object transformTuple(Object[] tuple, String[] aliases) {
                                System.err.println("tuple length = " + tuple.length);
                                for (int i = 0; i < tuple.length; i++) {
                                    System.err.println(i + ": " + tuple[i] + " (" + tuple[i].getClass() + ")");
                                }
                                return new SimpleSurveillanceStatus((Number)tuple[0], (Number)tuple[1], (Number)tuple[2]);
                            }

                            @SuppressWarnings("rawtypes")
                            public List transformList(List collection) {
                                return collection;
                            }
                		    
                		})
                        .uniqueResult();
            }

        });

    }


    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getForeignIdToNodeIdMap(String foreignSource) {
        List<Object[]> pairs = getHibernateTemplate().find("select n.id, n.foreignId from OnmsNode n where n.foreignSource = ?", foreignSource);
        Map<String, Integer> foreignIdMap = new HashMap<String, Integer>();
        for (Object[] pair : pairs) {
            foreignIdMap.put((String)pair[1], (Integer)pair[0]);
        }
        return foreignIdMap;
    }

    /** {@inheritDoc} */
    public List<OnmsNode> findByForeignSource(String foreignSource) {
        return find("from OnmsNode n where n.foreignSource = ?", foreignSource);
    }

    /** {@inheritDoc} */
    public OnmsNode findByForeignId(String foreignSource, String foreignId) {
        return findUnique("from OnmsNode n where n.foreignSource = ? and n.foreignId = ?", foreignSource, foreignId);
    }
    
    /** {@inheritDoc} */
    public List<OnmsNode> findByForeignSourceAndIpAddress(String foreignSource, String ipAddress) {
        if (foreignSource == null) {
            return find("select distinct n from OnmsNode n join n.ipInterfaces as ipInterface where n.foreignSource is NULL and ipInterface.inetAddress = ?", ipAddress);
        } else {
            return find("select distinct n from OnmsNode n join n.ipInterfaces as ipInterface where n.foreignSource = ? and ipInterface.inetAddress = ?", foreignSource, ipAddress);
        }
    }

    /** {@inheritDoc} */
    public int getNodeCountForForeignSource(String foreignSource) {
        return queryInt("select count(*) from OnmsNode as n where n.foreignSource = ?", foreignSource);
    }
    
    /**
     * <p>findAll</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<OnmsNode> findAll() {
        return find("from OnmsNode order by label");
    }
    
    /**
     * <p>findAllProvisionedNodes</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<OnmsNode> findAllProvisionedNodes() {
        return find("from OnmsNode n where n.foreignSource is not null");
    }
    
    /** {@inheritDoc} */
    public List<OnmsIpInterface> findObsoleteIpInterfaces(Integer nodeId, Date scanStamp) {
        return findObjects(OnmsIpInterface.class, "from OnmsIpInterface ipInterface where ipInterface.node.id = ? and (ipInterface.ipLastCapsdPoll is null or ipInterface.ipLastCapsdPoll < ?)", nodeId, scanStamp);
    }

    /** {@inheritDoc} */
    public void deleteObsoleteInterfaces(Integer nodeId, Date scanStamp) {
        getHibernateTemplate().bulkUpdate("delete from OnmsIpInterface ipInterface where ipInterface.node.id = ? and (ipInterface.ipLastCapsdPoll is null or ipInterface.ipLastCapsdPoll < ?)", new Object[] { nodeId, scanStamp });
        getHibernateTemplate().bulkUpdate("delete from OnmsSnmpInterface ipInterface where ipInterface.node.id = ? and (ipInterface.lastCapsdPoll is null or ipInterface.lastCapsdPoll < ?)", new Object[] { nodeId, scanStamp });
    }

    /** {@inheritDoc} */
    public void updateNodeScanStamp(Integer nodeId, Date scanStamp) {
        OnmsNode n = get(nodeId);
        n.setLastCapsdPoll(scanStamp);
        update(n);
    }

    /**
     * <p>getNodeIds</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Integer> getNodeIds() {
        return findObjects(Integer.class, "select distinct n.id from OnmsNode as n where n.type != 'D'");
    }


    
    


}
