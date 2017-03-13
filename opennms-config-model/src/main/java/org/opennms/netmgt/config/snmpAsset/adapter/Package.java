/*******************************************************************************
 * This file is part of OpenNMS(R).
 * 
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
 * 
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * 
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *     http://www.gnu.org/licenses/
 * 
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config.snmpAsset.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class Package.
 * 
 * @version $Revision$ $Date$
 */
@XmlRootElement(name = "package")
@XmlAccessorType(XmlAccessType.FIELD)
public class Package implements java.io.Serializable {
    private static final long serialVersionUID = 3805218232933347563L;

    @XmlAttribute(name = "name", required = true)
    private String name;

    /**
     * System object identifier (sysoid) which uniquely
     *  identifies the system.
     */
    @XmlElement(name = "sysoid")
    private String sysoid;

    /**
     * Sysoid mask which can be used to match multiple
     *  systems if their sysoid begins with the mask
     */
    @XmlElement(name = "sysoidMask")
    private String sysoidMask;

    @XmlElement(name = "assetField", required = true)
    private List<AssetField> assetFieldList = new ArrayList<>();

    /**
     * 
     * 
     * @param vAssetField
     * @throws IndexOutOfBoundsException if the index given is outside
     * the bounds of the collection
     */
    public void addAssetField(final AssetField vAssetField) throws IndexOutOfBoundsException {
        this.assetFieldList.add(vAssetField);
    }

    /**
     * 
     * 
     * @param index
     * @param vAssetField
     * @throws IndexOutOfBoundsException if the index given is outside
     * the bounds of the collection
     */
    public void addAssetField(final int index, final AssetField vAssetField) throws IndexOutOfBoundsException {
        this.assetFieldList.add(index, vAssetField);
    }

    /**
     * Method enumerateAssetField.
     * 
     * @return an Enumeration over all possible elements of this collection
     */
    public Enumeration<AssetField> enumerateAssetField() {
        return Collections.enumeration(this.assetFieldList);
    }

    /**
     * Overrides the Object.equals method.
     * 
     * @param obj
     * @return true if the objects are equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        
        if (obj instanceof Package) {
            Package temp = (Package)obj;
            boolean equals = Objects.equals(temp.name, name)
                && Objects.equals(temp.sysoid, sysoid)
                && Objects.equals(temp.sysoidMask, sysoidMask)
                && Objects.equals(temp.assetFieldList, assetFieldList);
            return equals;
        }
        return false;
    }

    /**
     * Method getAssetField.
     * 
     * @param index
     * @throws IndexOutOfBoundsException if the index given is outside
     * the bounds of the collection
     * @return the value of the
     * AssetField at the given index
     */
    public AssetField getAssetField(final int index) throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.assetFieldList.size()) {
            throw new IndexOutOfBoundsException("getAssetField: Index value '" + index + "' not in range [0.." + (this.assetFieldList.size() - 1) + "]");
        }
        
        return (AssetField) assetFieldList.get(index);
    }

    /**
     * Method getAssetField.Returns the contents of the collection in an Array. 
     * <p>Note:  Just in case the collection contents are changing in another
     * thread, we pass a 0-length Array of the correct type into the API call. 
     * This way we <i>know</i> that the Array returned is of exactly the correct
     * length.
     * 
     * @return this collection as an Array
     */
    public AssetField[] getAssetField() {
        AssetField[] array = new AssetField[0];
        return (AssetField[]) this.assetFieldList.toArray(array);
    }

    /**
     * Method getAssetFieldCollection.Returns a reference to 'assetFieldList'. No
     * type checking is performed on any modifications to the Vector.
     * 
     * @return a reference to the Vector backing this class
     */
    public List<AssetField> getAssetFieldCollection() {
        return this.assetFieldList;
    }

    /**
     * Method getAssetFieldCount.
     * 
     * @return the size of this collection
     */
    public int getAssetFieldCount() {
        return this.assetFieldList.size();
    }

    /**
     * Returns the value of field 'name'.
     * 
     * @return the value of field 'Name'.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the value of field 'sysoid'. The field 'sysoid' has the following
     * description: System object identifier (sysoid) which uniquely
     *  identifies the system.
     * 
     * @return the value of field 'Sysoid'.
     */
    public String getSysoid() {
        return this.sysoid;
    }

    /**
     * Returns the value of field 'sysoidMask'. The field 'sysoidMask' has the
     * following description: Sysoid mask which can be used to match multiple
     *  systems if their sysoid begins with the mask
     * 
     * @return the value of field 'SysoidMask'.
     */
    public String getSysoidMask() {
        return this.sysoidMask;
    }

    /**
     * Method hashCode.
     * 
     * @return a hash code value for the object.
     */
    @Override
    public int hashCode() {
        int hash = Objects.hash(
            name, 
            sysoid,
            sysoidMask,
            assetFieldList);
        return hash;
    }

    /**
     * Method iterateAssetField.
     * 
     * @return an Iterator over all possible elements in this collection
     */
    public Iterator<AssetField> iterateAssetField() {
        return this.assetFieldList.iterator();
    }

    /**
     */
    public void removeAllAssetField() {
        this.assetFieldList.clear();
    }

    /**
     * Method removeAssetField.
     * 
     * @param vAssetField
     * @return true if the object was removed from the collection.
     */
    public boolean removeAssetField(final AssetField vAssetField) {
        boolean removed = assetFieldList.remove(vAssetField);
        return removed;
    }

    /**
     * Method removeAssetFieldAt.
     * 
     * @param index
     * @return the element removed from the collection
     */
    public AssetField removeAssetFieldAt(final int index) {
        Object obj = this.assetFieldList.remove(index);
        return (AssetField) obj;
    }

    /**
     * 
     * 
     * @param index
     * @param vAssetField
     * @throws IndexOutOfBoundsException if the index given is outside
     * the bounds of the collection
     */
    public void setAssetField(final int index, final AssetField vAssetField) throws IndexOutOfBoundsException {
        // check bounds for index
        if (index < 0 || index >= this.assetFieldList.size()) {
            throw new IndexOutOfBoundsException("setAssetField: Index value '" + index + "' not in range [0.." + (this.assetFieldList.size() - 1) + "]");
        }
        
        this.assetFieldList.set(index, vAssetField);
    }

    /**
     * 
     * 
     * @param vAssetFieldArray
     */
    public void setAssetField(final AssetField[] vAssetFieldArray) {
        //-- copy array
        assetFieldList.clear();
        
        for (int i = 0; i < vAssetFieldArray.length; i++) {
                this.assetFieldList.add(vAssetFieldArray[i]);
        }
    }

    /**
     * Sets the value of 'assetFieldList' by copying the given Vector. All
     * elements will be checked for type safety.
     * 
     * @param vAssetFieldList the Vector to copy.
     */
    public void setAssetField(final List<AssetField> vAssetFieldList) {
        // copy vector
        this.assetFieldList.clear();
        
        this.assetFieldList.addAll(vAssetFieldList);
    }

    /**
     * Sets the value of 'assetFieldList' by setting it to the given Vector. No
     * type checking is performed.
     * @deprecated
     * 
     * @param assetFieldList the Vector to set.
     */
    public void setAssetFieldCollection(final List<AssetField> assetFieldList) {
        this.assetFieldList = assetFieldList;
    }

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the value of field 'sysoid'. The field 'sysoid' has the following
     * description: System object identifier (sysoid) which uniquely
     *  identifies the system.
     * 
     * @param sysoid the value of field 'sysoid'.
     */
    public void setSysoid(final String sysoid) {
        this.sysoid = sysoid;
    }

    /**
     * Sets the value of field 'sysoidMask'. The field 'sysoidMask' has the
     * following description: Sysoid mask which can be used to match multiple
     *  systems if their sysoid begins with the mask
     * 
     * @param sysoidMask the value of field 'sysoidMask'.
     */
    public void setSysoidMask(final String sysoidMask) {
        this.sysoidMask = sysoidMask;
    }
}
