package in.kriger.newkrigercampus.activities;
import java.util.HashMap;
import java.util.Map;



public class Newcertification {

   // @JsonProperty("description")
    private String description;
  //  @JsonProperty("name")
    private String name;
   // @JsonProperty("year_from")
    private String yearFrom;
   // @JsonProperty("_id")
    private String id;
   // @JsonProperty("is_visible")
    private Integer isVisible;
   // @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

   // @JsonProperty("description")
    public String getDescription() {
        return description;
    }

   // @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

   // @JsonProperty("name")
    public String getName() {
        return name;
    }

   // @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

  //  @JsonProperty("year_from")
    public String getYearFrom() {
        return yearFrom;
    }

    //@JsonProperty("year_from")
    public void setYearFrom(String yearFrom) {
        this.yearFrom = yearFrom;
    }

    //@JsonProperty("_id")
    public String getId() {
        return id;
    }

    //@JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

   // @JsonProperty("is_visible")
    public Integer getIsVisible() {
        return isVisible;
    }

  //  @JsonProperty("is_visible")
    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

   // @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

   // @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

