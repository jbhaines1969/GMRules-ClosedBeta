/*
 FILE CONTRACT (Non-Null):
 - All fields are initialized and remain non-null.
 - External absent values are normalized immediately.
 - Collections are returned by copy.
*/
// NONNULL_CONTRACT

package com.gamemaker.gmrules.GameMechanics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Creator-authored operation that may adjust generated Attribute rolls before assignment. */
public final class RollAdjustmentMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_FIXED_VALUE = "fixed_value";
    public static final String TYPE_RAISE_HIGHEST = "raise_highest";
    public static final String TYPE_TRANSFER = "transfer";
    public static final String TYPE_SPEND_RESOURCE = "spend_resource";

    private String id = UUID.randomUUID().toString();
    private String name = "";
    private String description = "";
    private String type = TYPE_FIXED_VALUE;
    private int value = 0;
    private int maximumUses = 1;
    private int sourceCostPerUnit = 1;
    private int targetGainPerUnit = 1;
    private int sourceMinimum = 0;
    private int targetMaximum = 0;
    private String resourceKey = "";
    private String resourceName = "";
    private int resourceBudget = 0;
    private int resourceCostPerPoint = 1;

    public RollAdjustmentMethod() {
    }

    public RollAdjustmentMethod(String name, String type) {
        setName(name);
        setType(type);
    }

    public RollAdjustmentMethod(String id, String name, String type) {
        setId(id);
        setName(name);
        setType(type);
    }

    public RollAdjustmentMethod(RollAdjustmentMethod source) {
        RollAdjustmentMethod safeSource = Objects.requireNonNullElseGet(
            source,
            RollAdjustmentMethod::new
        );
        setId(safeSource.id);
        setName(safeSource.name);
        setDescription(safeSource.description);
        setType(safeSource.type);
        setValue(safeSource.value);
        setMaximumUses(safeSource.maximumUses);
        setSourceCostPerUnit(safeSource.sourceCostPerUnit);
        setTargetGainPerUnit(safeSource.targetGainPerUnit);
        setSourceMinimum(safeSource.sourceMinimum);
        setTargetMaximum(safeSource.targetMaximum);
        setResourceKey(safeSource.resourceKey);
        setResourceName(safeSource.resourceName);
        setResourceBudget(safeSource.resourceBudget);
        setResourceCostPerPoint(safeSource.resourceCostPerPoint);
    }

    public String getId() { return id; }
    public void setId(String id) {
        String normalized = Objects.toString(id, "").trim();
        this.id = normalized.isEmpty() ? UUID.randomUUID().toString() : normalized;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = Objects.toString(name, "").trim(); }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = Objects.toString(description, "").trim();
    }

    public String getType() { return type; }
    public void setType(String type) {
        String normalized = Objects.toString(type, "").trim().toLowerCase();
        this.type = switch (normalized) {
            case TYPE_RAISE_HIGHEST, TYPE_TRANSFER, TYPE_SPEND_RESOURCE -> normalized;
            default -> TYPE_FIXED_VALUE;
        };
    }

    /** Fixed replacement value or the floor used by Raise Highest. */
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public int getMaximumUses() { return maximumUses; }
    public void setMaximumUses(int maximumUses) { this.maximumUses = Math.max(1, maximumUses); }

    public int getSourceCostPerUnit() { return sourceCostPerUnit; }
    public void setSourceCostPerUnit(int sourceCostPerUnit) {
        this.sourceCostPerUnit = Math.max(1, sourceCostPerUnit);
    }

    public int getTargetGainPerUnit() { return targetGainPerUnit; }
    public void setTargetGainPerUnit(int targetGainPerUnit) {
        this.targetGainPerUnit = Math.max(1, targetGainPerUnit);
    }

    public int getSourceMinimum() { return sourceMinimum; }
    public void setSourceMinimum(int sourceMinimum) { this.sourceMinimum = sourceMinimum; }

    /** Zero means that this adjustment does not impose a target ceiling. */
    public int getTargetMaximum() { return targetMaximum; }
    public void setTargetMaximum(int targetMaximum) {
        this.targetMaximum = Math.max(0, targetMaximum);
    }

    public String getResourceKey() { return resourceKey; }
    public void setResourceKey(String resourceKey) {
        this.resourceKey = Objects.toString(resourceKey, "").trim();
    }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) {
        this.resourceName = Objects.toString(resourceName, "").trim();
    }

    public int getResourceBudget() { return resourceBudget; }
    public void setResourceBudget(int resourceBudget) {
        this.resourceBudget = Math.max(0, resourceBudget);
    }

    public int getResourceCostPerPoint() { return resourceCostPerPoint; }
    public void setResourceCostPerPoint(int resourceCostPerPoint) {
        this.resourceCostPerPoint = Math.max(1, resourceCostPerPoint);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setId(id);
        setName(name);
        setDescription(description);
        setType(type);
        setMaximumUses(maximumUses);
        setSourceCostPerUnit(sourceCostPerUnit);
        setTargetGainPerUnit(targetGainPerUnit);
        setTargetMaximum(targetMaximum);
        setResourceKey(resourceKey);
        setResourceName(resourceName);
        setResourceBudget(resourceBudget);
        setResourceCostPerPoint(resourceCostPerPoint);
    }

    /** One generated value with an identity that survives every adjustment. */
    public static final class RollValue {
        private final String id;
        private final int value;

        public RollValue(String id, int value) {
            this.id = Objects.toString(id, "").trim();
            this.value = value;
        }

        public String getId() { return id; }
        public int getValue() { return value; }
    }

    /** Player decisions and the adjustment accounting already accepted by core. */
    public static final class Request {
        private String methodId = "";
        private String targetValueId = "";
        private String sourceValueId = "";
        private int amount = 1;
        private List<RollValue> values = new ArrayList<>();
        private Map<String, Integer> usesByMethod = new LinkedHashMap<>();
        private Map<String, Integer> resourceSpentByKey = new LinkedHashMap<>();

        public Request() {
        }

        public Request(Request source) {
            Request safeSource = Objects.requireNonNullElseGet(source, Request::new);
            setMethodId(safeSource.methodId);
            setTargetValueId(safeSource.targetValueId);
            setSourceValueId(safeSource.sourceValueId);
            setAmount(safeSource.amount);
            setValues(safeSource.values);
            setUsesByMethod(safeSource.usesByMethod);
            setResourceSpentByKey(safeSource.resourceSpentByKey);
        }

        public String getMethodId() { return methodId; }
        public void setMethodId(String methodId) {
            this.methodId = Objects.toString(methodId, "").trim();
        }

        public String getTargetValueId() { return targetValueId; }
        public void setTargetValueId(String targetValueId) {
            this.targetValueId = Objects.toString(targetValueId, "").trim();
        }

        public String getSourceValueId() { return sourceValueId; }
        public void setSourceValueId(String sourceValueId) {
            this.sourceValueId = Objects.toString(sourceValueId, "").trim();
        }

        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = Math.max(1, amount); }

        public List<RollValue> getValues() { return copyValues(values); }
        public void setValues(Collection<RollValue> values) {
            this.values = copyValues(values);
        }

        public Map<String, Integer> getUsesByMethod() {
            return new LinkedHashMap<>(usesByMethod);
        }
        public void setUsesByMethod(Map<String, Integer> usesByMethod) {
            this.usesByMethod = copyNonNegativeMap(usesByMethod);
        }

        public Map<String, Integer> getResourceSpentByKey() {
            return new LinkedHashMap<>(resourceSpentByKey);
        }
        public void setResourceSpentByKey(Map<String, Integer> resourceSpentByKey) {
            this.resourceSpentByKey = copyNonNegativeMap(resourceSpentByKey);
        }
    }

    /** Core-described legal decision shape for one creator-authored method. */
    public static final class Option {
        private final String methodId;
        private final String name;
        private final String description;
        private final String type;
        private final boolean available;
        private final String reason;
        private final int usesRemaining;
        private final boolean targetRequired;
        private final boolean sourceRequired;
        private final boolean amountRequired;
        private final List<String> legalTargetValueIds;
        private final List<String> legalSourceValueIds;
        private final int minimumAmount;
        private final int maximumAmount;
        private final String resourceKey;
        private final String resourceName;
        private final int resourceRemaining;

        Option(
            RollAdjustmentMethod method,
            boolean available,
            String reason,
            int usesRemaining,
            boolean targetRequired,
            boolean sourceRequired,
            boolean amountRequired,
            Collection<String> legalTargetValueIds,
            Collection<String> legalSourceValueIds,
            int maximumAmount,
            int resourceRemaining
        ) {
            RollAdjustmentMethod safeMethod = new RollAdjustmentMethod(method);
            this.methodId = safeMethod.getId();
            this.name = safeMethod.getName();
            this.description = safeMethod.getDescription();
            this.type = safeMethod.getType();
            this.available = available;
            this.reason = Objects.toString(reason, "");
            this.usesRemaining = Math.max(0, usesRemaining);
            this.targetRequired = targetRequired;
            this.sourceRequired = sourceRequired;
            this.amountRequired = amountRequired;
            this.legalTargetValueIds = List.copyOf(Objects.requireNonNullElse(legalTargetValueIds, List.of()));
            this.legalSourceValueIds = List.copyOf(Objects.requireNonNullElse(legalSourceValueIds, List.of()));
            this.minimumAmount = amountRequired ? 1 : 0;
            this.maximumAmount = Math.max(this.minimumAmount, maximumAmount);
            this.resourceKey = safeMethod.getResourceKey();
            this.resourceName = safeMethod.getResourceName();
            this.resourceRemaining = Math.max(0, resourceRemaining);
        }

        public String getMethodId() { return methodId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public boolean isAvailable() { return available; }
        public String getReason() { return reason; }
        public int getUsesRemaining() { return usesRemaining; }
        public boolean isTargetRequired() { return targetRequired; }
        public boolean isSourceRequired() { return sourceRequired; }
        public boolean isAmountRequired() { return amountRequired; }
        public List<String> getLegalTargetValueIds() { return List.copyOf(legalTargetValueIds); }
        public List<String> getLegalSourceValueIds() { return List.copyOf(legalSourceValueIds); }
        public int getMinimumAmount() { return minimumAmount; }
        public int getMaximumAmount() { return maximumAmount; }
        public String getResourceKey() { return resourceKey; }
        public String getResourceName() { return resourceName; }
        public int getResourceRemaining() { return resourceRemaining; }
    }

    /** Authoritative values, accounting, deltas, and next legal operations. */
    public static final class Result {
        private final boolean applied;
        private final List<RollValue> values;
        private final List<Option> options;
        private final Map<String, Integer> usesByMethod;
        private final Map<String, Integer> resourceSpentByKey;
        private final Map<String, Integer> valueDeltas;
        private final Map<String, Integer> resourceCosts;
        private final String reason;

        Result(
            boolean applied,
            Collection<RollValue> values,
            Collection<Option> options,
            Map<String, Integer> usesByMethod,
            Map<String, Integer> resourceSpentByKey,
            Map<String, Integer> valueDeltas,
            Map<String, Integer> resourceCosts,
            String reason
        ) {
            this.applied = applied;
            this.values = copyValues(values);
            this.options = List.copyOf(Objects.requireNonNullElse(options, List.of()));
            this.usesByMethod = copyNonNegativeMap(usesByMethod);
            this.resourceSpentByKey = copyNonNegativeMap(resourceSpentByKey);
            this.valueDeltas = new LinkedHashMap<>(Objects.requireNonNullElse(valueDeltas, Map.of()));
            this.resourceCosts = copyNonNegativeMap(resourceCosts);
            this.reason = Objects.toString(reason, "");
        }

        public boolean isApplied() { return applied; }
        public List<RollValue> getValues() { return copyValues(values); }
        public List<Option> getOptions() { return List.copyOf(options); }
        public Map<String, Integer> getUsesByMethod() { return new LinkedHashMap<>(usesByMethod); }
        public Map<String, Integer> getResourceSpentByKey() {
            return new LinkedHashMap<>(resourceSpentByKey);
        }
        public Map<String, Integer> getValueDeltas() { return new LinkedHashMap<>(valueDeltas); }
        public Map<String, Integer> getResourceCosts() { return new LinkedHashMap<>(resourceCosts); }
        public String getReason() { return reason; }
    }

    static ArrayList<RollValue> copyValues(Collection<RollValue> source) {
        Collection<RollValue> safeSource = Objects.requireNonNullElse(source, List.of());
        ArrayList<RollValue> copy = new ArrayList<>();
        for (RollValue value : safeSource) {
            RollValue safeValue = Objects.requireNonNull(value, "rollValue");
            copy.add(new RollValue(safeValue.getId(), safeValue.getValue()));
        }
        return copy;
    }

    static LinkedHashMap<String, Integer> copyNonNegativeMap(Map<String, Integer> source) {
        Map<String, Integer> safeSource = Objects.requireNonNullElse(source, Map.of());
        LinkedHashMap<String, Integer> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeSource.entrySet()) {
            String key = Objects.toString(entry.getKey(), "").trim();
            if (!key.isEmpty()) {
                copy.put(key, Math.max(0, Objects.requireNonNullElse(entry.getValue(), 0)));
            }
        }
        return copy;
    }
}
