// 
// Decompiled by Procyon v0.5.36
// 

package oe;

public class HL7Value
{
    private String version;
    private String parentGroup;
    private String groupName;
    private String structureName;
    private String structureNumber;
    private String fieldName;
    private String coordinate;
    private String dataType;
    private String description;
    private String value;
    
    public HL7Value(final String version, final String parentGroup, final String groupName, final String structureName, final String structureNumber, final String fieldName, final String coordinate, final String dataType, final String description, final String value) {
        this.version = version;
        this.parentGroup = parentGroup;
        this.groupName = groupName;
        this.structureName = structureName;
        this.structureNumber = structureNumber;
        this.fieldName = fieldName;
        this.coordinate = coordinate;
        this.dataType = dataType;
        this.description = description;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.structureNumber.trim()) + ":" + this.fieldName.trim() + "--" + this.description + ":" + this.structureName + "-" + this.coordinate + ":" + NVL(this.value, "") + "\n";
    }
    
    public String toString1() {
        return String.valueOf(this.version) + " (" + this.parentGroup + ") " + this.groupName + " " + this.structureName + " " + this.structureNumber + " : " + this.fieldName + " " + this.coordinate + " (" + this.dataType + " " + this.description + ") : " + NVL(this.value, "");
    }
    
    public static String NVL(final String source, final String def) {
        if (source == null || source.length() == 0) {
            return def;
        }
        return source;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public String getParentGroup() {
        return this.parentGroup;
    }
    
    public void setParentGroup(final String parentGroup) {
        this.parentGroup = parentGroup;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }
    
    public String getStructureName() {
        return this.structureName;
    }
    
    public void setStructureName(final String structureName) {
        this.structureName = structureName;
    }
    
    public String getFieldName() {
        return this.fieldName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getDataType() {
        return this.dataType;
    }
    
    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getStructureNumber() {
        return this.structureNumber;
    }
    
    public void setStructureNumber(final String structureNumber) {
        this.structureNumber = structureNumber;
    }
    
    public String getCoordinate() {
        return this.coordinate;
    }
    
    public void setCoordinate(final String coordinate) {
        this.coordinate = coordinate;
    }
}
