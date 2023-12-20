package SEPee.server.model.gameBoard;

import SEPee.server.model.field.*;

import java.util.ArrayList;
import java.util.List;

public class LostBearings extends GameBoard {
    public LostBearings() {

        super("1A", "LostBearings", 13, 4);

        // column 0
        List<List<Field>> column0 = new ArrayList<>(10);

        List<Field> field0_0 = new ArrayList<>();
        field0_0.add(new Empty("A"));

        List<Field> field0_1 = new ArrayList<>();
        field0_1.add(new Empty("A"));

        List<Field> field0_2 = new ArrayList<>();
        field0_2.add(new Empty("A"));

        List<Field> field0_3 = new ArrayList<>();
        field0_3.add(new StartPoint("A"));
        String[] orientations03 = {"bottom"};
        field0_3.add(new Wall("A", orientations03));

        List<Field> field0_4 = new ArrayList<>();
        String[] orientations04 = {"right"};
        field0_4.add(new Antenna("A", orientations04));

        List<Field> field0_5 = new ArrayList<>();
        //field0_5.add(new Empty("A"));
        String[] orientations05 = {"top"};
        field0_5.add(new Wall("A", orientations05));

        List<Field> field0_6 = new ArrayList<>();
        field0_6.add(new StartPoint("A"));

        List<Field> field0_7 = new ArrayList<>();
        field0_7.add(new Empty("A"));

        List<Field> field0_8 = new ArrayList<>();
        field0_8.add(new Empty("A"));

        List<Field> field0_9 = new ArrayList<>();
        field0_9.add(new Empty("A"));

        column0.add(field0_0);
        column0.add(field0_1);
        column0.add(field0_2);
        column0.add(field0_3);
        column0.add(field0_4);
        column0.add(field0_5);
        column0.add(field0_6);
        column0.add(field0_7);
        column0.add(field0_8);
        column0.add(field0_9);

        this.addRow(column0);

        // column 1
        List<List<Field>> column1 = new ArrayList<>(10);

        List<Field> field1_0 = new ArrayList<>();
        field1_0.add(new Empty("A"));

        List<Field> field1_1 = new ArrayList<>();
        String[] orientations11Wall = {"bottom"};
        field1_1.add(new StartPoint("A"));
        field1_1.add(new Wall("A", orientations11Wall));

        List<Field> field1_2 = new ArrayList<>();
        String[] orientations12 = {"top"};
        field1_2.add(new Wall("A", orientations12));

        List<Field> field1_3 = new ArrayList<>();
        field1_3.add(new Empty("A"));

        List<Field> field1_4 = new ArrayList<>();
        field1_4.add(new StartPoint("A"));
        String[] orientations14 = {"left"};
        field1_4.add(new Wall("A", orientations14));

        List<Field> field1_5 = new ArrayList<>();
        field1_5.add(new StartPoint("A"));

        List<Field> field1_6 = new ArrayList<>();
        field1_6.add(new Empty("A"));

        List<Field> field1_7 = new ArrayList<>();
        String[] orientations17 = {"bottom"};
        field1_7.add(new Wall("A", orientations17));

        List<Field> field1_8 = new ArrayList<>();
        String[] orientations18Wall = {"top"};
        field1_8.add(new StartPoint("A"));
        field1_8.add(new Wall("A", orientations18Wall));

        List<Field> field1_9 = new ArrayList<>();
        field1_9.add(new Empty("A"));

        column1.add(field1_0);
        column1.add(field1_1);
        column1.add(field1_2);
        column1.add(field1_3);
        column1.add(field1_4);
        column1.add(field1_5);
        column1.add(field1_6);
        column1.add(field1_7);
        column1.add(field1_8);
        column1.add(field1_9);

        this.addRow(column1);

        // column 2
        List<List<Field>> column2 = new ArrayList<>(10);

        List<Field> field2_0 = new ArrayList<>();
        String[] orientations20 = {"right", "left"};
        field2_0.add(new ConveyorBelt("A", 1, orientations20));

        List<Field> field2_1 = new ArrayList<>();
        field2_1.add(new Empty("A"));

        List<Field> field2_2 = new ArrayList<>();
        field2_2.add(new Empty("A"));

        List<Field> field2_3 = new ArrayList<>();
        field2_3.add(new Empty("A"));

        List<Field> field2_4 = new ArrayList<>();
        String[] orientations24 = {"right"};
        field2_4.add(new Wall("A", orientations24));

        List<Field> field2_5 = new ArrayList<>();
        String[] orientations25 = {"right"};
        field2_5.add(new Wall("A", orientations25));

        List<Field> field2_6 = new ArrayList<>();
        field2_6.add(new Empty("A"));

        List<Field> field2_7 = new ArrayList<>();
        field2_7.add(new Empty("A"));

        List<Field> field2_8 = new ArrayList<>();
        field2_8.add(new Empty("A"));

        List<Field> field2_9 = new ArrayList<>();
        String[] orientations29 = {"right", "left"};
        field2_9.add(new ConveyorBelt("A", 1, orientations29));

        column2.add(field2_0);
        column2.add(field2_1);
        column2.add(field2_2);
        column2.add(field2_3);
        column2.add(field2_4);
        column2.add(field2_5);
        column2.add(field2_6);
        column2.add(field2_7);
        column2.add(field2_8);
        column2.add(field2_9);

        this.addRow(column2);

        // column 3
        List<List<Field>> column3 = new ArrayList<>(10);

        List<Field> field3_0 = new ArrayList<>();
        field3_0.add(new Empty("1A"));

        List<Field> field3_1 = new ArrayList<>();
        String[] orientations31Belt = {"left, right"};
        field3_1.add(new ConveyorBelt("1A", 1, orientations31Belt));

        List<Field> field3_2 = new ArrayList<>();
        field3_2.add(new Empty("1A"));

        List<Field> field3_3 = new ArrayList<>();
        field3_3.add(new Empty("1A"));

        List<Field> field3_4 = new ArrayList<>();
        String[] orientations34Wall = {"left"};
        field3_4.add(new Wall("1A", orientations34Wall));

        List<Field> field3_5 = new ArrayList<>();
        String[] orientations35Wall = {"left"};
        field3_5.add(new Wall("1A", orientations35Wall));

        List<Field> field3_6 = new ArrayList<>();
        field3_6.add(new Empty("1A"));

        List<Field> field3_7 = new ArrayList<>();
        field3_7.add(new Empty("1A"));

        List<Field> field3_8 = new ArrayList<>();
        String[] orientations38Belt = {"right, left"};
        field3_8.add(new ConveyorBelt("1A", 1, orientations38Belt));

        List<Field> field3_9 = new ArrayList<>();
        field3_9.add(new Empty("1A"));

        column3.add(field3_0);
        column3.add(field3_1);
        column3.add(field3_2);
        column3.add(field3_3);
        column3.add(field3_4);
        column3.add(field3_5);
        column3.add(field3_6);
        column3.add(field3_7);
        column3.add(field3_8);
        column3.add(field3_9);

        this.addRow(column3);

        // column 4
        List<List<Field>> column4 = new ArrayList<>(10);

        List<Field> field4_0 = new ArrayList<>();
        String[] orientations40 = {"bottom", "top"};
        field4_0.add(new ConveyorBelt("1A", 1, orientations40));

        List<Field> field4_1 = new ArrayList<>();
        String[] orientations41 = {"left", "top"};
        field4_1.add(new ConveyorBelt("1A", 1, orientations41));

        List<Field> field4_2 = new ArrayList<>();
        field4_2.add(new Empty("1A"));

        List<Field> field4_3 = new ArrayList<>();
        field4_3.add(new Empty("1A"));

        List<Field> field4_4 = new ArrayList<>();
        field4_4.add(new Empty("1A"));

        List<Field> field4_5 = new ArrayList<>();
        field4_5.add(new CheckPoint("1A"));

        List<Field> field4_6 = new ArrayList<>();
        field4_6.add(new Empty("1A"));

        List<Field> field4_7 = new ArrayList<>();
        field4_7.add(new Empty("1A"));

        List<Field> field4_8 = new ArrayList<>();
        String[] orientations48 = {"bottom", "left"};
        field4_8.add(new ConveyorBelt("1A", 1, orientations48));

        List<Field> field4_9 = new ArrayList<>();
        String[] orientations49 = {"bottom", "top"};
        field4_9.add(new ConveyorBelt("1A", 1, orientations49));

        column4.add(field4_0);
        column4.add(field4_1);
        column4.add(field4_2);
        column4.add(field4_3);
        column4.add(field4_4);
        column4.add(field4_5);
        column4.add(field4_6);
        column4.add(field4_7);
        column4.add(field4_8);
        column4.add(field4_9);

        this.addRow(column4);

        // column 5
        List<List<Field>> column5 = new ArrayList<>(10);

        List<Field> field5_0 = new ArrayList<>();
        field5_0.add(new Empty("1A"));

        List<Field> field5_1 = new ArrayList<>();
        field5_1.add(new Empty("1A"));

        List<Field> field5_2 = new ArrayList<>();
        field5_2.add(new EnergySpace("1A", 1));

        List<Field> field5_3 = new ArrayList<>();
        String[] orientations53 = {"right", "left"};
        field5_3.add(new ConveyorBelt("1A", 2, orientations53));
        String[] orientations53Laser = {"top"};
        field5_3.add(new Laser("1A", orientations53Laser,1));

        List<Field> field5_4 = new ArrayList<>();
        String[] orientations54Laser = {"top"};
        field5_4.add(new Laser("1A", orientations54Laser,1));

        List<Field> field5_5 = new ArrayList<>();
        String[] orientations55Laser = {"top"};
        field5_5.add(new Laser("1A", orientations55Laser,1));
        String[] orientations55Wall = {"right"};
        field5_5.add(new Wall("1A", orientations55Wall));

        List<Field> field5_6 = new ArrayList<>();
        String[] orientations56Belt = {"right, left"};
        field5_6.add(new ConveyorBelt("1A", 2, orientations56Belt));
        String[] orientations56Laser = {"top"};
        field5_6.add(new Laser("1A", orientations56Laser,1));

        List<Field> field5_7 = new ArrayList<>();
        field5_7.add(new CheckPoint("1A"));
        String[] orientations57Wall = {"bottom"};
        field5_7.add(new Wall("1A", orientations57Wall));

        List<Field> field5_8 = new ArrayList<>();
        String[] orientations58Wall = {"top"};
        field5_8.add(new Wall("1A", orientations58Wall));

        List<Field> field5_9 = new ArrayList<>();
        String[] orientations59Laser = {"right"};
        field5_9.add(new Laser("1A", orientations59Laser,1));

        column5.add(field5_0);
        column5.add(field5_1);
        column5.add(field5_2);
        column5.add(field5_3);
        column5.add(field5_4);
        column5.add(field5_5);
        column5.add(field5_6);
        column5.add(field5_7);
        column5.add(field5_8);
        column5.add(field5_9);

        this.addRow(column5);

        // column 6
        List<List<Field>> column6 = new ArrayList<>(10);

        List<Field> field6_0 = new ArrayList<>();
        field6_0.add(new Empty("4A"));

        List<Field> field6_1 = new ArrayList<>();
        String[] orientations61 = {"right", "bottom"};
        field6_1.add(new ConveyorBelt("4A", 1, orientations61));

        List<Field> field6_2 = new ArrayList<>();
        field6_2.add(new Pit("4A"));

        List<Field> field6_3 = new ArrayList<>();
        String[] orientations63Wall = {"right"};
        field6_3.add(new Wall("4A", orientations63Wall));
        field6_3.add(new Pit("4A"));

        List<Field> field6_4 = new ArrayList<>();
        String[] orientations64Gear = {"clockwise"};
        field6_4.add(new Wall("4A", orientations64Gear));

        List<Field> field6_5 = new ArrayList<>();
        String[] orientations65Wall = {"left"};
        field6_5.add(new Wall("4A", orientations65Wall));

        List<Field> field6_6 = new ArrayList<>();
        field6_6.add(new Pit("4A"));
        String[] orientations66Wall = {"right"};
        field6_6.add(new Wall("4A", orientations66Wall));

        List<Field> field6_7 = new ArrayList<>();
        field6_7.add(new Pit("4A"));

        List<Field> field6_8 = new ArrayList<>();
        field6_8.add(new Empty("4A"));

        List<Field> field6_9 = new ArrayList<>();
        String[] orientations69Wall = {"right"};
        String[] orientations69Laser = {"left"};
        field6_9.add(new Wall("4A", orientations69Wall));
        field6_9.add(new Laser("4A", orientations69Laser, 1));

        column6.add(field6_0);
        column6.add(field6_1);
        column6.add(field6_2);
        column6.add(field6_3);
        column6.add(field6_4);
        column6.add(field6_5);
        column6.add(field6_6);
        column6.add(field6_7);
        column6.add(field6_8);
        column6.add(field6_9);

        this.addRow(column6);

        // column 7
        List<List<Field>> column7 = new ArrayList<>(10);

        List<Field> field7_0 = new ArrayList<>();
        field7_0.add(new Empty("4A"));

        List<Field> field7_1 = new ArrayList<>();
        String[] orientations71 = {"right", "left"};
        field7_1.add(new ConveyorBelt("4A", 1, orientations71));

        List<Field> field7_2 = new ArrayList<>();
        field7_2.add(new Empty("4A"));

        List<Field> field7_3 = new ArrayList<>();
        String[] orientations73Laser = {"right"};
        String[] orientations73Wall = {"left"};
        field7_3.add(new Wall("4A", orientations73Wall));
        field7_3.add(new Laser("4A", orientations73Laser, 1));

        List<Field> field7_4 = new ArrayList<>();
        field7_4.add(new Empty("4A"));

        List<Field> field7_5 = new ArrayList<>();
        field7_5.add(new EnergySpace("4A", 1));

        List<Field> field7_6 = new ArrayList<>();
        String[] orientations76Wall = {"left"};
        String[] orientations76Laser = {"right"};
        field7_6.add(new Wall("4A", orientations76Wall));
        field7_6.add(new Laser("4A", orientations76Laser, 1));

        List<Field> field7_7 = new ArrayList<>();
        field7_7.add(new Empty("4A"));

        List<Field> field7_8 = new ArrayList<>();
        field7_8.add(new Empty("4A"));

        List<Field> field7_9 = new ArrayList<>();
        String[] orientations79Wall = {"left"};
        field7_9.add(new Wall("4A", orientations79Wall));

        column7.add(field7_0);
        column7.add(field7_1);
        column7.add(field7_2);
        column7.add(field7_3);
        column7.add(field7_4);
        column7.add(field7_5);
        column7.add(field7_6);
        column7.add(field7_7);
        column7.add(field7_8);
        column7.add(field7_9);

        this.addRow(column7);

        // column 8
        List<List<Field>> column8 = new ArrayList<>(10);

        List<Field> field8_0 = new ArrayList<>();
        field8_0.add(new EnergySpace("4A", 1));
        String[] orientations80Wall = {"right"};
        field8_0.add(new Wall("4A", orientations80Wall));

        List<Field> field8_1 = new ArrayList<>();
        field8_1.add(new Empty("4A"));

        List<Field> field8_2 = new ArrayList<>();
        field8_2.add(new Empty("4A"));

        List<Field> field8_3 = new ArrayList<>();
        String[] orientations83Wall = {"right"};
        String[] orientations83Laser = {"left"};
        field8_3.add(new Wall("4A", orientations83Wall));
        field8_3.add(new Laser("4A", orientations83Laser, 1));

        List<Field> field8_4 = new ArrayList<>();
        field8_4.add(new Empty("4A"));

        List<Field> field8_5 = new ArrayList<>();
        field8_5.add(new Empty("4A"));

        List<Field> field8_6 = new ArrayList<>();
        String[] orientations86Wall = {"right"};
        String[] orientations86Laser = {"left"};
        field8_6.add(new Wall("4A", orientations86Wall));
        field8_6.add(new Laser("4A", orientations86Laser, 1));

        List<Field> field8_7 = new ArrayList<>();
        field8_7.add(new Empty("4A"));

        List<Field> field8_8 = new ArrayList<>();
        String[] orientations88 = {"right", "left"};
        field8_8.add(new ConveyorBelt("4A", 1, orientations88));

        List<Field> field8_9 = new ArrayList<>();
        field8_9.add(new Empty("4A"));

        column8.add(field8_0);
        column8.add(field8_1);
        column8.add(field8_2);
        column8.add(field8_3);
        column8.add(field8_4);
        column8.add(field8_5);
        column8.add(field8_6);
        column8.add(field8_7);
        column8.add(field8_8);
        column8.add(field8_9);

        this.addRow(column8);

        // column 9
        List<List<Field>> column9 = new ArrayList<>(10);

        List<Field> field9_0 = new ArrayList<>();
        String[] orientations90Wall = {"left"};
        String[] orientations90Laser = {"right"};
        field9_0.add(new Wall("4A", orientations90Wall));
        field9_0.add(new Laser("4A", orientations90Laser,1));

        List<Field> field9_1 = new ArrayList<>();
        field9_1.add(new Empty("4A"));

        List<Field> field9_2 = new ArrayList<>();
        field9_2.add(new Pit("4A"));

        List<Field> field9_3 = new ArrayList<>();
        String[] orientations93Wall = {"left"};
        field9_3.add(new Wall("4A", orientations93Wall));
        field9_3.add(new Pit("4A"));

        List<Field> field9_4 = new ArrayList<>();
        String[] orientations94Wall = {"right"};
        field9_4.add(new Wall("4A", orientations94Wall));

        List<Field> field9_5 = new ArrayList<>();
        String[] orientations95Gear = {"counterclockwise"};
        field9_5.add(new Gear("4A", orientations95Gear));

        List<Field> field9_6 = new ArrayList<>();
        String[] orientations96Wall = {"left"};
        field9_6.add(new Wall("4A", orientations96Wall));
        field9_6.add(new Pit("4A"));

        List<Field> field9_7 = new ArrayList<>();
        field9_7.add(new Pit("4A"));

        List<Field> field9_8 = new ArrayList<>();
        String[] orientations98 = {"top", "left"};
        field9_8.add(new ConveyorBelt("4A", 1, orientations98));

        List<Field> field9_9 = new ArrayList<>();
        field9_9.add(new Empty("4A"));

        column9.add(field9_0);
        column9.add(field9_1);
        column9.add(field9_2);
        column9.add(field9_3);
        column9.add(field9_4);
        column9.add(field9_5);
        column9.add(field9_6);
        column9.add(field9_7);
        column9.add(field9_8);
        column9.add(field9_9);
        this.addRow(column9);

        // column 10
        List<List<Field>> column10 = new ArrayList<>(10);

        List<Field> field10_0 = new ArrayList<>();
        String[] orientation100Laser = {"right"};
        field10_0.add(new Laser("4A", orientation100Laser, 1));

        List<Field> field10_1 = new ArrayList<>();
        String[] orientation101Wall = {"bottom"};
        field10_1.add(new Wall("4A", orientation101Wall));

        List<Field> field10_2 = new ArrayList<>();
        field10_2.add(new CheckPoint("4A"));
        String[] orientation102Wall = {"top"};
        field10_2.add(new Wall("4A", orientation102Wall));

        List<Field> field10_3 = new ArrayList<>();
        String[] orientations103Laser = {"top"};
        field10_3.add(new Laser("4A", orientations103Laser, 1));
        String[] orientation103Belt = {"left, right"};
        field10_3.add(new ConveyorBelt("4A", 2, orientation103Belt));

        List<Field> field10_4 = new ArrayList<>();
        String[] orientations104Laser = {"top"};
        field10_4.add(new Laser("4A", orientations104Laser, 1));
        String[] orientation104Wall = {"left"};
        field10_4.add(new Wall("4A", orientation104Wall));

        List<Field> field10_5 = new ArrayList<>();
        String[] orientation105Wall = {"top"};
        field10_5.add(new Wall("4A", orientation105Wall));

        List<Field> field10_6 = new ArrayList<>();
        String[] orientation106Belt = {"left, right"};
        field10_6.add(new ConveyorBelt("4A", 2, orientation106Belt));

        List<Field> field10_7 = new ArrayList<>();
        field10_7.add(new CheckPoint("4A"));
        String[] orientation107Wall = {"bottom"};
        field10_7.add(new Wall("4A", orientation107Wall));

        List<Field> field10_8 = new ArrayList<>();
        String[] orientation108Wall = {"top"};
        field10_8.add(new Wall("4A", orientation108Wall));

        List<Field> field10_9 = new ArrayList<>();
        field10_9.add(new Empty("4A"));

        column10.add(field10_0);
        column10.add(field10_1);
        column10.add(field10_2);
        column10.add(field10_3);
        column10.add(field10_4);
        column10.add(field10_5);
        column10.add(field10_6);
        column10.add(field10_7);
        column10.add(field10_8);
        column10.add(field10_9);

        this.addRow(column10);

        // column 11
        List<List<Field>> column11 = new ArrayList<>(10);

        List<Field> field11_0 = new ArrayList<>();
        String[] orientations110Wall = {"right"};
        String[] orientations110Laser = {"left"};
        field11_0.add(new Wall("4A", orientations110Wall));
        field11_0.add(new Laser("4A", orientations110Laser,1));

        List<Field> field11_1 = new ArrayList<>();
        String[] orientations111 = {"bottom", "top"};
        field11_1.add(new ConveyorBelt("4A", 2, orientations111));

        List<Field> field11_2 = new ArrayList<>();
        String[] orientations112 = {"bottom", "top"};
        field11_2.add(new ConveyorBelt("4A", 2, orientations112));

        List<Field> field11_3 = new ArrayList<>();
        String[] orientations113 = {"left", "top"};
        field11_3.add(new ConveyorBelt("4A", 2, orientations113));

        List<Field> field11_4 = new ArrayList<>();
        field11_4.add(new EnergySpace("4A",1));

        List<Field> field11_5 = new ArrayList<>();
        field11_5.add(new Empty("4A"));

        List<Field> field11_6 = new ArrayList<>();
        String[] orientations116 = {"left", "bottom"};
        field11_6.add(new ConveyorBelt("4A", 2, orientations116));

        List<Field> field11_7 = new ArrayList<>();
        String[] orientations117 = {"top", "bottom"};
        field11_7.add(new ConveyorBelt("4A", 2, orientations117));

        List<Field> field11_8 = new ArrayList<>();
        String[] orientations118 = {"top", "bottom"};
        field11_8.add(new ConveyorBelt("4A", 2, orientations118));

        List<Field> field11_9 = new ArrayList<>();
        String[] orientations119 = {"top", "bottom"};
        field11_9.add(new ConveyorBelt("4A", 2, orientations119));

        column11.add(field11_0);
        column11.add(field11_1);
        column11.add(field11_2);
        column11.add(field11_3);
        column11.add(field11_4);
        column11.add(field11_5);
        column11.add(field11_6);
        column11.add(field11_7);
        column11.add(field11_8);
        column11.add(field11_9);

        this.addRow(column11);

        // column 12
        List<List<Field>> column12 = new ArrayList<>(10);

        List<Field> field12_0 = new ArrayList<>();
        String[] orientations120Wall = {"left"};
        field12_0.add(new Wall("4A", orientations120Wall));

        List<Field> field12_1 = new ArrayList<>();
        field12_1.add(new Empty("4A"));

        List<Field> field12_2 = new ArrayList<>();
        field12_2.add(new Empty("4A"));

        List<Field> field12_3 = new ArrayList<>();
        String[] orientations123Wall = {"bottom"};
        field12_3.add(new Wall("4A", orientations123Wall));

        List<Field> field12_4 = new ArrayList<>();
        String[] orientations124Wall = {"top"};
        field12_4.add(new Wall("4A", orientations124Wall));

        List<Field> field12_5 = new ArrayList<>();
        String[] orientations125Wall = {"bottom"};
        field12_5.add(new Wall("4A", orientations125Wall));

        List<Field> field12_6 = new ArrayList<>();
        String[] orientations126Wall = {"top"};
        field12_6.add(new Wall("4A", orientations126Wall));

        List<Field> field12_7 = new ArrayList<>();
        field12_7.add(new Empty("4A"));

        List<Field> field12_8 = new ArrayList<>();
        field12_8.add(new Empty("4A"));

        List<Field> field12_9 = new ArrayList<>();
        field12_9.add(new Empty("4A"));

        column12.add(field12_0);
        column12.add(field12_1);
        column12.add(field12_2);
        column12.add(field12_3);
        column12.add(field12_4);
        column12.add(field12_5);
        column12.add(field12_6);
        column12.add(field12_7);
        column12.add(field12_8);
        column12.add(field12_9);

        this.addRow(column12);
    }



    public List<Field> getFieldsAt(int x, int y) {
        if (x < 0 || x >= this.getGameBoard().size()) {
            throw new IllegalArgumentException("X-coordinate out of bounds");
        }

        List<List<Field>> column = this.getGameBoard().get(x);
        if (y < 0 || y >= column.size()) {
            throw new IllegalArgumentException("Y-coordinate out of bounds");
        }

        List<Field> fieldsAtXY = column.get(y);
        return fieldsAtXY;
    }





}
