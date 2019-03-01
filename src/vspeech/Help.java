/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vspeech;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author acer
 */
public class Help {
    private final SimpleStringProperty strName;
    private final SimpleStringProperty strCategory;
    private final SimpleStringProperty strDescription;
 
        Help(String name, String category, String description) {
            this.strName = new SimpleStringProperty(name);
            this.strCategory = new SimpleStringProperty(category);
            this.strDescription = new SimpleStringProperty(description);
        }
 
        public String getName() {
            return strName.get();
        }
 
        public void setName(String name) {
            strName.set(name);
        }
 
        public String getCategory() {
            return strCategory.get();
        }
 
        public void setCategory(String category) {
            strCategory.set(category);
        }
        
        public String getDescription() {
            return strDescription.get();
        }
 
        public void setDescription(String description) {
            strDescription.set(description);
        }
}
