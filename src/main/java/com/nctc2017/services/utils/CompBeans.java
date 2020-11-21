package com.nctc2017.services.utils;

import com.nctc2017.bean.ShipTemplate;
import com.nctc2017.bean.StartShipEquipment;
import com.nctc2017.bean.StartTypeOfShipEquip;

public class CompBeans {
    public class ShipTemplateCompare implements java.util.Comparator<ShipTemplate> {

        @Override
        public int compare(ShipTemplate o1, ShipTemplate o2) {
            return Integer.compare(o1.getTemplateId().intValue(), o2.getTemplateId().intValue());
        }
    }

    public class StartTypeCompare implements java.util.Comparator<StartTypeOfShipEquip> {
        @Override
        public int compare(StartTypeOfShipEquip o1, StartTypeOfShipEquip o2) {
            return Integer.compare(o1.getShipTempId().intValue(), o2.getShipTempId().intValue());
        }
    }

    public class StartShipEquipCompare implements java.util.Comparator<StartShipEquipment> {
        @Override
        public int compare(StartShipEquipment o1, StartShipEquipment o2) {
            return Integer.compare(o1.getShipTId().intValue(), o2.getShipTId().intValue());
        }
    }
}
