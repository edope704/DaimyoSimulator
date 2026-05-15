package it.unipd.daimyosimulator.core.persistence.dto;

import java.util.ArrayList;
import java.util.List;

public final class VillageDTO {
    public int version = 1;
    public long tickNumber;
    public GridDTO grid;
    public ResourceStockDTO resources;
    public VillageParametersDTO parameters;
    public List<VillagerDTO> villagers = new ArrayList<>();
    public PolicyDTO policy;
    public int birthProgress;
    public int starvationTicks;
    public boolean randomEventsEnabled;
    public List<String> eventHistory = new ArrayList<>();
}
