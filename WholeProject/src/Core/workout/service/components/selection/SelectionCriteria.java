package model.workout.service.components.selection;

import model.workout.enums.MuscleGroup;
import model.workout.enums.PreferredEquipment;
import java.util.List;


/**
 * Parameter object encapsulating all criteria for exercise selection.
 * Reduces method signature complexity and makes it easier to add new criteria.
 */
public class SelectionCriteria {
    private final MuscleGroup muscleGroup;
    private final List<PreferredEquipment> equipmentPreferences;
    private final RotationTracker rotationTracker;

    private final int limit;
    private final int currentDay;
    
    private SelectionCriteria(Builder builder) {
        this.muscleGroup = builder.muscleGroup;
        this.equipmentPreferences = builder.equipmentPreferences;
        this.rotationTracker = builder.rotationTracker;

        this.limit = builder.limit;
        this.currentDay = builder.currentDay;
    }
    
    public MuscleGroup getMuscleGroup() {
        return muscleGroup;
    }
    
    public List<PreferredEquipment> getEquipmentPreferences() {
        return equipmentPreferences;
    }
    
    public RotationTracker getRotationTracker() {
        return rotationTracker;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public int getCurrentDay() {
        return currentDay;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private MuscleGroup muscleGroup;
        private List<PreferredEquipment> equipmentPreferences;
        private RotationTracker rotationTracker;
        private int limit;
        private int currentDay;
        
        public Builder muscleGroup(MuscleGroup muscleGroup) {
            this.muscleGroup = muscleGroup;
            return this;
        }
        
        public Builder equipmentPreferences(List<PreferredEquipment> equipmentPreferences) {
            this.equipmentPreferences = equipmentPreferences;
            return this;
        }
        
        public Builder rotationTracker(RotationTracker rotationTracker) {
            this.rotationTracker = rotationTracker;
            return this;
        }
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }
        
        public Builder currentDay(int currentDay) {
            this.currentDay = currentDay;
            return this;
        }
        
        public SelectionCriteria build() {
            return new SelectionCriteria(this);
        }
    }
}
