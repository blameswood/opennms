/*******************************************************************************
 * This file is part of OpenNMS(R).
 * <p>
 * Copyright (C) 2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * <p>
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * <p>
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 * http://www.gnu.org/licenses/
 * <p>
 * For more information contact:
 * OpenNMS(R) Licensing <license@opennms.org>
 * http://www.opennms.org/
 * http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest.api.support;

import org.opennms.web.rest.api.model.ApiVersion;
import org.opennms.web.rest.api.model.ResourceLocation;

/**
 * Factory to create {@link ResourceLocation} objects for certain REST endpoints.
 */
public class ResourceLocationFactory {

    public static ResourceLocation createIpServiceLocation(String serviceId) {
        return new ResourceLocation(ApiVersion.Version1, "ifservices", serviceId);
    }

    public static ResourceLocation createBusinessServiceLocation(String id) {
        return new ResourceLocation(ApiVersion.Version2, "business-services", id);
    }

    public static ResourceLocation createBusinessServiceLocation() {
        return new ResourceLocation(ApiVersion.Version2, "business-services");
    }

    public static ResourceLocation createNodeLocation(String nodeCriteria) {
        return new ResourceLocation(ApiVersion.Version1, "nodes", nodeCriteria);
    }

    public static ResourceLocation createIpInterfaceLocation(String nodeCriteria, String ipAddress) {
        return new ResourceLocation(ApiVersion.Version1, "nodes", nodeCriteria, "ipinterfaces", ipAddress);
    }

    public static ResourceLocation createIpInterfaceLocation(String nodeCriteria) {
        return new ResourceLocation(ApiVersion.Version1, "nodes", nodeCriteria, "ipinterfaces");
    }

    public static ResourceLocation createIpServiceLocation(String nodeCriteria, String ipAddress, String serviceId) {
        return new ResourceLocation(ApiVersion.Version1, "nodes", nodeCriteria, "ipinterfaces", ipAddress, "services", serviceId);
    }

    public static ResourceLocation createIpServiceLocation(String nodeCriteria, String ipAddress) {
        return new ResourceLocation(ApiVersion.Version1, "nodes", nodeCriteria, "ipinterfaces", ipAddress, "services");
    }
}