package assignment3;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;

import assignment3.*;

import java.lang.reflect.Field;
import java.util.Arrays;

public class BlockTest {

    static String BLOCK_CHILDREN_FIELD_NAME = "children"; //this should be set to the name of your block's "children" field
    static String BLOCK_XCOORD_FIELD_NAME = "xCoord";
    static String BLOCK_YCOORD_FIELD_NAME = "yCoord";
    static String BLOCK_SIZE_FIELD_NAME = "size";

    public Field privateChildrenField;
    public Field privateXCoordField;
    public Field privateYCoordField;
    public Field privateSizeField;


    {
        try {
            privateChildrenField = Block.class.getDeclaredField(BLOCK_CHILDREN_FIELD_NAME);
            privateChildrenField.setAccessible(true);

            privateXCoordField = Block.class.getDeclaredField(BLOCK_XCOORD_FIELD_NAME);
            privateXCoordField.setAccessible(true);

            privateYCoordField = Block.class.getDeclaredField(BLOCK_YCOORD_FIELD_NAME);
            privateYCoordField.setAccessible(true);

            privateSizeField = Block.class.getDeclaredField(BLOCK_SIZE_FIELD_NAME);
            privateSizeField.setAccessible(true);
        }
        catch(NoSuchFieldException ignored){
            // I know, printing when I said not to. This is not a test, you just need this for the tests to work :/
            System.out.println("The global field names must match your Block's field names");
        }
    }

    @Test
    public void Block_randomConstructor_instantiates(){
        Block myBlock = new Block(0, 3);
    }


    @ParameterizedTest
    @Tag("BlockTest") @DisplayName("randomConstructor throws when negative level")
    @ValueSource(strings = {"-1", "-3", "-4", "-5555"})
    void Block_randomConstructor_throwsIllegalArgumentExceptionWhenNegativeLevel(Integer targetLevel) {
        assertThrows(IllegalArgumentException.class, ()->{
            Block myBlock = new Block(targetLevel, 3);
        });
    }

    @ParameterizedTest
    @Tag("BlockTest") @DisplayName("randomConstructor throws when non positive max depth")
    @ValueSource(strings = {"0", "-1", "-3", "-4", "-5555"})
    void Block_randomConstructor_throwsIllegalArgumentExceptionWhenNonPositiveMaxDepth(Integer targetMaxDepth) {
        assertThrows(IllegalArgumentException.class, ()->{
            Block myBlock = new Block(0, targetMaxDepth);
        });
    }

    @ParameterizedTest
    @Tag("BlockTest") @DisplayName("randomConstructor throws when level is greater than than max depth")
    @ValueSource(strings = {"2", "3", "4", "5", "6"})
    void Block_randomConstructor_throwsIllegalArgumentExceptionWhenLevelGreaterThanMaxDepth(Integer targetLevel) {
        assertThrows(IllegalArgumentException.class, ()->{
            Block myBlock = new Block(targetLevel, 1);
        });
    }


    @Test
    public void Block_randomConstructor_randomSmashingWorksAsExpected() throws IllegalAccessException {
        // This is more of an integration test and kind of goes against the tester's guidelines. I apologize,
        // but I still think it's better to have it and I didn't find a better way to test this.
        // If you find a better way to verify that the random Block constructor generates sub-block in the right way,
        // please implement it. - Julien
        Block myBlock = new Block(0, 2);
        //this test requires seeding the random generator with 2 as in: Random(2);
        //levels are the only verifiable/replicatable elements of the teacher's example on page 9
        Block[] childrens = (Block[]) privateChildrenField.get(myBlock);


        assertNotNull(childrens);

        assertEquals(0, myBlock.getLevel());
        assertEquals(4, childrens.length);

        // level 1 children
        for(int i = 0; i<4; i++){
            assertEquals(1, childrens[i].getLevel());
        }

        //childrens of the fourth children: level 2 children
        childrens = (Block[])privateChildrenField.get(childrens[3]);
        assertEquals(4, childrens.length);
        for(int i = 0; i<4; i++){
            assertEquals(2, childrens[i].getLevel());
        }
    }

    @Test
    public void Block_updateSizeAndPosition_WorksAsExpected() throws IllegalAccessException {
        // This is more of an integration test and kind of goes against the tester's guidelines. I apologize,
        // but I still think it's better to have it and I didn't find a better way to test this.
        // If you find a better way to verify that the random Block constructor generates sub-block in the right way,
        // please implement it. - Julien
        Block myBlock = new Block(0, 2);
        myBlock.updateSizeAndPosition(16, 0, 0);

        //this test requires seeding the random generator with 2 as in: Random(2);
        //levels are the only verifiable/replicatable elements of the teacher's example on page 10
        assertArrayEquals(new int[]{0, 0}, getBlockCoordinates(myBlock));
        assertEquals(16, getBlockSize(myBlock));

        Block[] childrens = (Block[]) privateChildrenField.get(myBlock);

        //checking coordinates of lvl1 children
        assertArrayEquals(new int[]{8, 0}, (getBlockCoordinates(childrens[0])));
        assertArrayEquals(new int[]{0, 0}, getBlockCoordinates(childrens[1]));
        assertArrayEquals(new int[]{0, 8}, getBlockCoordinates(childrens[2]));
        assertArrayEquals(new int[]{8, 8}, getBlockCoordinates(childrens[3]));

        //checking size of lvl1 children
        for(int i = 0; i<4; i++){
            assertEquals(8, getBlockSize(childrens[i]));
        }

        Block[] level2Childrens = (Block[]) privateChildrenField.get(childrens[3]);

        //checking coordinates of lvl2 children
        assertArrayEquals(new int[]{12, 8}, getBlockCoordinates(level2Childrens[0]));
        assertArrayEquals(new int[]{8, 8}, getBlockCoordinates(level2Childrens[1]));
        assertArrayEquals(new int[]{8, 12}, getBlockCoordinates(level2Childrens[2]));
        assertArrayEquals(new int[]{12, 12}, getBlockCoordinates(level2Childrens[3]));

        //checking size of lvl2 children
        for(int i = 0; i<4; i++){
            assertEquals(4, getBlockSize(level2Childrens[i]));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-4", "-8", "-16"})
    void Block_updateSizeAndPosition_throwsIllegalArgumentExceptionWhenSizeNonPositive(Integer targetSize) {
        assertThrows(IllegalArgumentException.class, ()->{
            Block myBlock = new Block(0, 2);
            myBlock.updateSizeAndPosition(targetSize, 0, 0);
        });
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0, 3, 7",
            "0, 3, 6",
            "0, 3, 9",
            "0, 3, 14",
            "0, 3, 34",
            "8, 11, 7",
            "8, 11, 6",
            "8, 11, 9",
            "8, 11, 14",
            "8, 11, 34",
            "0, 4, 8",
            "0, 4, 127"
    }, ignoreLeadingAndTrailingWhitespace = true)
    void Block_updateSizeAndPosition_throwsIllegalArgumentExceptionWhenSizeNonDivisibleByPow2MaxDepth(Integer lvl, Integer maxDepth, Integer size) {
        assertThrows(IllegalArgumentException.class, ()->{
            Block myBlock = new Block(lvl, maxDepth);
            myBlock.updateSizeAndPosition(size, 0, 0);
        });
    }

    @ParameterizedTest
    @CsvSource(value = {
            "0, 3, 8",
            "0, 3, 16",
            "0, 3, 1024",
            "7, 10, 8",
            "7, 10, 16",
            "7, 10, 1024",
            "0, 1, 2",
            "0, 1, 1024",
            "6, 7, 2",
            "6, 7, 2",
    }, ignoreLeadingAndTrailingWhitespace = true)
    void Block_updateSizeAndPosition_worksWhenSizeDivisibleByPow2MaxDepth(Integer lvl, Integer maxDepth, Integer size) throws IllegalAccessException {
        Block myBlock = new Block(lvl, maxDepth);
        myBlock.updateSizeAndPosition(size, 0, 0);
        assertEquals(size, getBlockSize(myBlock));
    }

    private int[] getBlockCoordinates(Block block) throws IllegalAccessException {
        int[] coordinates = new int[2];
        coordinates[0] = (int) privateXCoordField.get(block);
        coordinates[1] = (int) privateYCoordField.get(block);
        return coordinates;
    }

    private int getBlockSize(Block block) throws IllegalAccessException{
        return (int) privateSizeField.get(block);
    }







}
