package SEPee.server.model.gameBoard;

import SEPee.server.model.field.*;

import java.util.ArrayList;
import java.util.List;

public class DeathTrap extends GameBoard {
    public DeathTrap() {

        super("2A", "DeathTrap", 13, 5);

        // column 0
        List<List<Field>> column0 = new ArrayList<>(10);

        List<Field> field0_0 = new ArrayList<>();
        field0_0.add(new Empty("2A"));

        List<Field> field0_1 = new ArrayList<>();
        field0_1.add(new CheckPoint("2A", 5));

        List<Field> field0_2 = new ArrayList<>();
        field0_2.add(new Empty("2A"));

        List<Field> field0_3 = new ArrayList<>();
        field0_3.add(new Empty("2A"));

        List<Field> field0_4 = new ArrayList<>();
        field0_4.add(new Empty("2A"));

        List<Field> field0_5 = new ArrayList<>();
        String[] orientations05Belt = {"bottom, right"};
        field0_5.add(new ConveyorBelt("2A", 1, orientations05Belt));

        List<Field> field0_6 = new ArrayList<>();
        String[] orientations06Belt = {"bottom, top"};
        field0_6.add(new ConveyorBelt("2A", 1, orientations06Belt));

        List<Field> field0_7 = new ArrayList<>();
        String[] orientations07Belt = {"bottom, top"};
        field0_7.add(new ConveyorBelt("2A", 1, orientations07Belt));

        List<Field> field0_8 = new ArrayList<>();
        String[] orientations08Belt = {"bottom, top"};
        field0_8.add(new ConveyorBelt("2A", 1, orientations08Belt));
        String[] orientations08Wall = {"right"};
        field0_8.add(new Wall("2A", orientations08Wall));

        List<Field> field0_9 = new ArrayList<>();
        field0_9.add(new Empty("2A"));

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
        String[] orientation10Wall = {"bottom"};
        field1_0.add(new Wall("2A", orientation10Wall));

        List<Field> field1_1 = new ArrayList<>();
        String[] orientations11Wall = {"top"};
        field1_1.add(new Wall("2A", orientations11Wall));
        String[] orientation11Pusher = {"top"};
        int[] register11 = {1, 3, 5};
        field1_1.add(new PushPanel("2A", orientation11Pusher, register11));

        List<Field> field1_2 = new ArrayList<>();
        field1_2.add(new Pit("2A"));

        List<Field> field1_3 = new ArrayList<>();
        field1_3.add(new Empty("2A"));

        List<Field> field1_4 = new ArrayList<>();
        String[] orientations14Belt = {"bottom, top"};
        field1_4.add(new ConveyorBelt("2A", 1, orientations14Belt));

        List<Field> field1_5 = new ArrayList<>();
        String[] orientations15Belt = {"left, top"};
        field1_5.add(new ConveyorBelt("2A", 1, orientations15Belt));

        List<Field> field1_6 = new ArrayList<>();
        field1_6.add(new Empty("2A"));

        List<Field> field1_7 = new ArrayList<>();
        field1_7.add(new CheckPoint("2A", 1));

        List<Field> field1_8 = new ArrayList<>();
        String[] orientations18Wall = {"left"};
        field1_8.add(new Wall("2A", orientations18Wall));
        String[] orientation18Pusher = {"left"};
        int[] register18 = {1, 3, 5};
        field1_8.add(new PushPanel("2A", orientation18Pusher, register18));

        List<Field> field1_9 = new ArrayList<>();
        field1_9.add(new Empty("2A"));

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
        String[] orientations20 = {"left", "right"};
        field2_0.add(new ConveyorBelt("2A", 1, orientations20));

        List<Field> field2_1 = new ArrayList<>();
        field2_1.add(new Empty("2A"));

        List<Field> field2_2 = new ArrayList<>();
        String[] orientations22Wall = {"left"};
        field2_2.add(new Wall("2A", orientations22Wall));
        String[] orientation22Pusher = {"left"};
        int[] register22 = {2, 4};
        field2_2.add(new PushPanel("2A", orientation22Pusher, register22));

        List<Field> field2_3 = new ArrayList<>();
        field2_3.add(new EnergySpace("2A", 1));

        List<Field> field2_4 = new ArrayList<>();
        field2_4.add(new Empty("2A"));

        List<Field> field2_5 = new ArrayList<>();
        field2_5.add(new Empty("2A"));

        List<Field> field2_6 = new ArrayList<>();
        field2_6.add(new Pit("2A"));

        List<Field> field2_7 = new ArrayList<>();
        String[] orientations27Wall = {"bottom"};
        field2_7.add(new Wall("2A", orientations27Wall));
        String[] orientation27Pusher = {"bottom"};
        int[] register27 = {2, 4};
        field2_7.add(new PushPanel("2A", orientation27Pusher, register27));

        List<Field> field2_8 = new ArrayList<>();
        field2_8.add(new Pit("2A"));

        List<Field> field2_9 = new ArrayList<>();
        field2_9.add(new Empty("2A"));

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
        String[] orientations30Belt = {"left, right"};
        field3_0.add(new ConveyorBelt("2A", 1, orientations30Belt));

        List<Field> field3_1 = new ArrayList<>();
        String[] orientations31Wall = {"bottom"};
        field3_1.add(new Wall("2A", orientations31Wall));

        List<Field> field3_2 = new ArrayList<>();
        field3_2.add(new Pit("2A"));

        List<Field> field3_3 = new ArrayList<>();
        field3_3.add(new Empty("2A"));

        List<Field> field3_4 = new ArrayList<>();
        field3_4.add(new Pit("2A"));

        List<Field> field3_5 = new ArrayList<>();
        String[] orientations35Wall = {"bottom"};
        field3_5.add(new Wall("2A", orientations35Wall));
        String[] orientations35Pusher = {"bottom"};
        int[] register35 = {1, 3, 5};
        field3_5.add(new PushPanel("2A", orientations35Pusher, register35));

        List<Field> field3_6 = new ArrayList<>();
        String[] orientations36Wall = {"top"};
        field3_6.add(new Wall("2A", orientations36Wall));

        List<Field> field3_7 = new ArrayList<>();
        field3_7.add(new EnergySpace("2A", 1));

        List<Field> field3_8 = new ArrayList<>();
        field3_8.add(new Empty("2A"));

        List<Field> field3_9 = new ArrayList<>();
        field3_9.add(new Empty("2A"));

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
        String[] orientations40 = {"left", "bottom"};
        field4_0.add(new ConveyorBelt("2A", 1, orientations40));

        List<Field> field4_1 = new ArrayList<>();
        String[] orientations41 = {"top", "right"};
        field4_1.add(new ConveyorBelt("2A", 1, orientations41));

        List<Field> field4_2 = new ArrayList<>();
        field4_2.add(new EnergySpace("2A", 1));

        List<Field> field4_3 = new ArrayList<>();
        String[] orientations43Wall = {"bottom"};
        field4_3.add(new Wall("2A", orientations43Wall));
        String[] orientations43 = {"top"};
        int[] register43 = {2, 4};
        field4_3.add(new PushPanel("2A", orientations43, register43));

        List<Field> field4_4 = new ArrayList<>();
        String[] orientations44Wall = {"top"};
        field4_4.add(new Wall("2A", orientations44Wall));
        field4_4.add(new Wall("2A", orientations43Wall));
        field4_4.add(new CheckPoint("2A", 2));

        List<Field> field4_5 = new ArrayList<>();
        field4_3.add(new Wall("2A", orientations44Wall));
        String[] orientations45 = {"bottom"};
        int[] register45 = {2, 4};
        field4_5.add(new PushPanel("2A", orientations45, register45));

        List<Field> field4_6 = new ArrayList<>();
        field4_6.add(new EnergySpace("2A", 1));

        List<Field> field4_7 = new ArrayList<>();
        field4_7.add(new Empty("2A"));

        List<Field> field4_8 = new ArrayList<>();
        String[] orientations48 = {"right", "left"};
        field4_8.add(new ConveyorBelt("2A", 1, orientations48));

        List<Field> field4_9 = new ArrayList<>();
        field4_9.add(new Empty("2A"));

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
        field5_0.add(new Empty("2A"));

        List<Field> field5_1 = new ArrayList<>();
        String[] orientations51 = {"left", "right"};
        field5_1.add(new ConveyorBelt("2A", 2, orientations51));

        List<Field> field5_2 = new ArrayList<>();
        field5_2.add(new Empty("2A"));

        List<Field> field5_3 = new ArrayList<>();
        field5_3.add(new Empty("2A"));

        List<Field> field5_4 = new ArrayList<>();
        field5_4.add(new Empty("2A"));

        List<Field> field5_5 = new ArrayList<>();
        String[] orientations55Wall = {"bottom"};
        field5_5.add(new Wall("2A", orientations55Wall));

        List<Field> field5_6 = new ArrayList<>();
        String[] orientations56Wall = {"top"};
        field5_6.add(new Wall("2A", orientations56Wall));

        List<Field> field5_7 = new ArrayList<>();
        field5_7.add(new Empty("2A"));

        List<Field> field5_8 = new ArrayList<>();
        String[] orientations58 = {"bottom", "left"};
        field5_8.add(new ConveyorBelt("2A", 2, orientations58));

        List<Field> field5_9 = new ArrayList<>();
        String[] orientations59 = {"right", "top"};
        field5_9.add(new ConveyorBelt("2A", 2, orientations59));

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
        field6_0.add(new Empty("2A"));

        List<Field> field6_1 = new ArrayList<>();
        field6_1.add(new Empty("2A"));

        List<Field> field6_2 = new ArrayList<>();
        field6_2.add(new EnergySpace("2A", 1));

        List<Field> field6_3 = new ArrayList<>();
        String[] orientations63Wall = {"bottom"};
        field6_3.add(new Wall("2A", orientations63Wall));
        field6_3.add(new Empty("2A"));

        List<Field> field6_4 = new ArrayList<>();
        String[] orientations64Wall = {"top"};
        field6_4.add(new Wall("2A", orientations64Wall));
        String[] orientations64 = {"bottom"};
        int[] register64 = {1, 3, 5};
        field6_4.add(new PushPanel("2A", orientations64, register64));

        List<Field> field6_5 = new ArrayList<>();
        field6_5.add(new Pit("2A"));

        List<Field> field6_6 = new ArrayList<>();
        field6_6.add(new Empty("2A"));

        List<Field> field6_7 = new ArrayList<>();
        field6_7.add(new Pit("2A"));

        List<Field> field6_8 = new ArrayList<>();
        String[] orientations68Wall = {"top"};
        field6_8.add(new Wall("2A", orientations68Wall));

        List<Field> field6_9 = new ArrayList<>();
        String[] orientations69 = {"right", "left"};
        field6_9.add(new ConveyorBelt("2A", 1, orientations69));

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
        field7_0.add(new Empty("2A"));

        List<Field> field7_1 = new ArrayList<>();
        field7_1.add(new Pit("2A"));

        List<Field> field7_2 = new ArrayList<>();
        String[] orientations72Wall = {"top"};
        field7_2.add(new Wall("2A", orientations72Wall));
        String[] orientations72 = {"bottom"};
        int[] register72 = {2, 4};
        field7_2.add(new PushPanel("2A", orientations72, register72));

        List<Field> field7_3 = new ArrayList<>();
        field7_3.add(new Pit("2A"));

        List<Field> field7_4 = new ArrayList<>();
        field7_4.add(new Empty("2A"));

        List<Field> field7_5 = new ArrayList<>();
        field7_5.add(new Empty("2A"));

        List<Field> field7_6 = new ArrayList<>();
        field7_6.add(new EnergySpace("2A", 1));

        List<Field> field7_7 = new ArrayList<>();
        String[] orientations77Wall = {"right"};
        field7_7.add(new Wall("2A", orientations77Wall));
        String[] orientations77 = {"left"};
        int[] register77 = {2, 4};
        field7_7.add(new PushPanel("2A", orientations77, register77));

        List<Field> field7_8 = new ArrayList<>();
        field7_8.add(new CheckPoint("2A", 3));

        List<Field> field7_9 = new ArrayList<>();
        String[] orientations79 = {"right", "left"};
        field7_9.add(new ConveyorBelt("2A", 1, orientations79));

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
        field8_0.add(new Empty("2A"));

        List<Field> field8_1 = new ArrayList<>();
        String[] orientations81Wall = {"right"};
        field8_1.add(new Wall("2A", orientations81Wall));
        String[] orientations81 = {"left"};
        int[] register81 = {1, 3, 5};
        field8_1.add(new PushPanel("2A", orientations81, register81));

        List<Field> field8_2 = new ArrayList<>();
        field8_2.add(new CheckPoint("2A", 4));

        List<Field> field8_3 = new ArrayList<>();
        String[] orientations83Wall = {"left"};
        field8_3.add(new Wall("2A", orientations83Wall));

        List<Field> field8_4 = new ArrayList<>();
        String[] orientations84 = {"right", "bottom"};
        field8_4.add(new ConveyorBelt("2A", 1, orientations84));

        List<Field> field8_5 = new ArrayList<>();
        String[] orientations85 = {"top", "bottom"};
        field8_5.add(new ConveyorBelt("2A", 1, orientations85));

        List<Field> field8_6 = new ArrayList<>();
        field8_6.add(new Empty("2A"));

        List<Field> field8_7 = new ArrayList<>();
        field8_7.add(new Pit("2A"));

        List<Field> field8_8 = new ArrayList<>();
        String[] orientations88Wall = {"bottom"};
        field8_8.add(new Wall("2A", orientations88Wall));
        String[] orientations88 = {"top"};
        int[] register88 = {1, 3, 5};
        field8_8.add(new PushPanel("2A", orientations88, register88));

        List<Field> field8_9 = new ArrayList<>();
        String[] orientations89 = {"right", "left"};
        field8_9.add(new ConveyorBelt("2A", 1, orientations89));

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
        field9_0.add(new Empty("2A"));

        List<Field> field9_1 = new ArrayList<>();
        String[] orientations91Wall = {"left"};
        field9_1.add(new Wall("2A", orientations91Wall));
        String[] orientations91 = {"top", "bottom"};
        field9_1.add(new ConveyorBelt("2A", 1, orientations91));

        List<Field> field9_2 = new ArrayList<>();
        field9_2.add(new ConveyorBelt("2A", 1, orientations91));

        List<Field> field9_3 = new ArrayList<>();
        field9_3.add(new ConveyorBelt("2A", 1, orientations91));

        List<Field> field9_4 = new ArrayList<>();
        String[] orientations94 = {"top", "left"};
        field9_4.add(new ConveyorBelt("2A", 1, orientations94));

        List<Field> field9_5 = new ArrayList<>();
        field9_5.add(new Empty("2A"));

        List<Field> field9_6 = new ArrayList<>();
        field9_6.add(new Empty("2A"));

        List<Field> field9_7 = new ArrayList<>();
        field9_7.add(new Empty("2A"));

        List<Field> field9_8 = new ArrayList<>();
        field9_8.add(new Empty("2A"));

        List<Field> field9_9 = new ArrayList<>();
        field9_9.add(new Empty("2A"));

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
        String[] orientations100 = {"left", "right"};
        field10_0.add(new ConveyorBelt("A", 1, orientations100));

        List<Field> field10_1 = new ArrayList<>();
        field10_1.add(new Empty("A"));

        List<Field> field10_2 = new ArrayList<>();
        field10_2.add(new Empty("A"));

        List<Field> field10_3 = new ArrayList<>();
        field10_3.add(new Empty("A"));

        List<Field> field10_4 = new ArrayList<>();
        String[] orientations104 = {"left"};
        field10_4.add(new Wall("A", orientations104));

        List<Field> field10_5 = new ArrayList<>();
        String[] orientations105 = {"left"};
        field10_5.add(new Wall("A", orientations105));

        List<Field> field10_6 = new ArrayList<>();
        field10_6.add(new Empty("A"));

        List<Field> field10_7 = new ArrayList<>();
        field10_7.add(new Empty("A"));

        List<Field> field10_8 = new ArrayList<>();
        field10_8.add(new Empty("A"));

        List<Field> field10_9 = new ArrayList<>();
        String[] orientations109 = {"left", "right"};
        field10_9.add(new ConveyorBelt("A", 1, orientations109));

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
        field11_0.add(new Empty("A"));

        List<Field> field11_1 = new ArrayList<>();
        String[] orientations111Wall = {"bottom"};
        field11_1.add(new Wall("A", orientations111Wall));
        field11_1.add(new StartPoint("A"));

        List<Field> field11_2 = new ArrayList<>();
        String[] orientations112 = {"top"};
        field11_2.add(new Wall("A", orientations112));

        List<Field> field11_3 = new ArrayList<>();
        field11_3.add(new Empty("A"));

        List<Field> field11_4 = new ArrayList<>();
        field11_4.add(new StartPoint("A"));

        List<Field> field11_5 = new ArrayList<>();
        String[] orientations115Wall = {"right"};
        field11_5.add(new Wall("A", orientations115Wall));
        field11_5.add(new StartPoint("A"));

        List<Field> field11_6 = new ArrayList<>();
        field11_6.add(new Empty("A"));

        List<Field> field11_7 = new ArrayList<>();
        String[] orientations117 = {"bottom"};
        field11_7.add(new Wall("A", orientations117));

        List<Field> field11_8 = new ArrayList<>();
        String[] orientations118Wall = {"top"};
        field11_8.add(new Wall("A", orientations118Wall));
        field11_8.add(new StartPoint("A"));

        List<Field> field11_9 = new ArrayList<>();
        field11_9.add(new Empty("A"));

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
        field12_0.add(new Empty("A"));

        List<Field> field12_1 = new ArrayList<>();
        field12_1.add(new Empty("A"));

        List<Field> field12_2 = new ArrayList<>();
        field12_2.add(new Empty("A"));

        List<Field> field12_3 = new ArrayList<>();
        field12_3.add(new StartPoint("A"));

        List<Field> field12_4 = new ArrayList<>();
        String[] orientations124Wall = {"bottom"};
        field12_4.add(new Wall("A", orientations124Wall));
        field12_4.add(new Empty("A"));

        List<Field> field12_5 = new ArrayList<>();
        String[] orientations125 = {"left"};
        field12_5.add(new Antenna("A", orientations125));

        List<Field> field12_6 = new ArrayList<>();
        String[] orientations126Wall = {"top"};
        field12_6.add(new Wall("A", orientations126Wall));
        field12_6.add(new StartPoint("A"));

        List<Field> field12_7 = new ArrayList<>();
        field12_7.add(new Empty("A"));

        List<Field> field12_8 = new ArrayList<>();
        field12_8.add(new Empty("A"));

        List<Field> field12_9 = new ArrayList<>();
        field12_9.add(new Empty("A"));

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


    public String checkRebootConditions(int xCoordinate, int yCoordinate) {
        String rebootTo = "continue";

        if ((yCoordinate < 0 && xCoordinate <= 9 ) || (xCoordinate < 0) || (yCoordinate > 10 && xCoordinate < 3)) {
            // top left condition, left condition, bottom left condition, weil starting board links ist
            rebootTo = "startingPoint";
        } else if ((yCoordinate < 0 && xCoordinate >= 3) || (xCoordinate > 12) || (yCoordinate > 10 && xCoordinate >= 3)) {
            // top right, right, bottom right conditions
            rebootTo = "rebootField";
        }
        return rebootTo;
    }

    public int getRebootX(){
        return 12;
    }

    public int getRebootY(){
        return 9;
    }



}
