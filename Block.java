package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

    private int xCoord;
    private int yCoord;
    private int size;
    private int level;
    private int maxDepth;
    private Color color;
    private Block[] children;

    public static Random gen = new Random();



    public Block() {}

    public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
        this.xCoord=x;
        this.yCoord=y;
        this.size=size;
        this.level=lvl;
        this.maxDepth = maxD;
        this.color=c;
        this.children = subBlocks;
    }


    public Block(int lvl, int maxDepth) {
        this.level = lvl;
        this.maxDepth = maxDepth;

        if (lvl > maxDepth || lvl < 0){
            throw new IllegalArgumentException("Wrong bro");
        }

        if (lvl < maxDepth) {
            double randomNumber = gen.nextDouble();

            if (randomNumber < Math.exp(-0.25 * lvl)) {
                this.children = new Block[4];
                for (int i = 0; i < 4; i++) {
                    this.children[i] = new Block(lvl + 1, maxDepth);
                }
            } else {
                this.children = new Block[0];
                int randomColorIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
                this.color = GameColors.BLOCK_COLORS[randomColorIndex];
            }
        }
        else {
            this.children = new Block[0];
            int randomColorIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
            this.color = GameColors.BLOCK_COLORS[randomColorIndex];
        }

    }



    public void updateSizeAndPosition (int size, int x, int y) {
        if (((size % 2 != 0) && size != 1) && level != maxDepth || size <= 0 || maxDepth <= 0){ // used to be size == 0 and it didn't check maxdepth
            throw new IllegalArgumentException("Invalid Size");
        }

        this.size = size;
        this.xCoord = x;
        this.yCoord = y;

        if (this.children != null) {

            int[][] offsets = {{size / 2, 0}, {0,0}, {0, size / 2}, {size / 2, size / 2}};

            for (int i = 0; i < children.length; i++){
                children[i].updateSizeAndPosition(size / 2, x + offsets[i][0], y + offsets[i][1]);
            }

        }

    }


    public ArrayList<BlockToDraw> getBlocksToDraw() {
        ArrayList<BlockToDraw> blocksToDraw = new ArrayList<>();

        if (children.length == 0) {
            blocksToDraw.add(new BlockToDraw(color, xCoord, yCoord, size, 0)); // Fill the block with its color
            blocksToDraw.add(new BlockToDraw(GameColors.FRAME_COLOR, xCoord, yCoord, size, 3)); // Draw the frame
        } else {
            for (Block child : children) {
                blocksToDraw.addAll(child.getBlocksToDraw());
            }
        }
        return blocksToDraw;
    }



    public BlockToDraw getHighlightedFrame() {
        return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
    }


    public Block getSelectedBlock(int x, int y, int lvl) { //WORKS!!
        if (lvl < this.level || lvl > this.maxDepth) {
            throw new IllegalArgumentException("Invalid level");
        }

        if (x < this.xCoord || x > this.size + this.xCoord ||
                y < this.yCoord || y > this.size + this.yCoord) {
            return null;
        }

        if (this.children.length == 0) {
            return this;
        }
        if (this.level != lvl){
            for (Block child : this.children) {
                Block selectedBlock = child.getSelectedBlock(x, y, lvl);
                if (selectedBlock != null) {
                    return selectedBlock;
                }
            }
            return getSelectedBlock(x,y,lvl);
        }
        else {
            return this;
        }

    }


    public void reflect(int direction) {
        if (direction != 0 && direction != 1) {
            throw new IllegalArgumentException("Invalid direction, must be 0 or 1");
        }
        if (children.length == 4)
        {
            Block[] flip = new Block[4];
            if (direction == 0) // flip along the x-axis
            {
                flip[0] = children[3];
                flip[1] = children[2];
                flip[2] = children[1];
                flip[3] = children[0];
                this.children = flip;
                for (Block child : children)
                {
                    child.reflect(direction);
                }
            }
            else // flip along the y-axis
            {
                flip[0] = children[1];
                flip[1] = children[0];
                flip[2] = children[3];
                flip[3] = children[2];
                this.children = flip;
                for (Block child : children)
                {
                    child.reflect(direction);
                }
            }
            updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
        }

    }


    /*
     * Rotate this Block and all its descendants.
     * If the input is 1, rotate clockwise. If 0, rotate
     * counterclockwise. If this Block has no children, do nothing.
     */
    public void rotate(int direction) {
        if (direction != 0 && direction != 1) {
            throw new IllegalArgumentException("Invalid direction, must be 0 or 1");
        }

        if (children.length == 0) {
            return;
        }

        if (children.length == 4){
            Block[] rotate = new Block[4];
            if (direction == 1) // clockwise
            {
                rotate[0] = children[1];
                rotate[1] = children[2];
                rotate[2] = children[3];
                rotate[3] = children[0];
                this.children = rotate;
                for (Block child : children)
                {
                    child.rotate(direction);
                }
            }
            else // counterclockwise
            {
                rotate[0] = children[3];
                rotate[1] = children[0];
                rotate[2] = children[1];
                rotate[3] = children[2];
                this.children = rotate;
                for (Block child : children)
                {
                    child.rotate(direction);
                }
            }
            updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
        }
    }



    /*
     * Smash this Block.
     *
     * If this Block can be smashed,
     * randomly generate four new children Blocks for it.
     * (If it already had children Blocks, discard them.)
     * Ensure that the invariants of the Blocks remain satisfied.
     *
     * A Block can be smashed iff it is not the top-level Block
     * and it is not already at the level of the maximum depth.
     *
     * Return True if this Block was smashed and False otherwise.
     *
     */
    public boolean smash() { //WORKS!! lets go
        if (this.level != 0 && this.level < this.maxDepth) {
            this.children = new Block[4];
            for (int i = 0; i < 4; i++) {
                this.children[i] = new Block(this.level + 1, this.maxDepth);
            }
            updateSizeAndPosition(this.size, this.xCoord, this.yCoord);
            return true;
        }
        return false;
    }



    /*
     * Return a two-dimensional array representing this Block as rows and columns of unit cells.
     *
     * Return and array arr where, arr[i] represents the unit cells in row i,
     * arr[i][j] is the color of unit cell in row i and column j.
     *
     * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
     */
    public Color[][] flatten() {
        int halfSquare = (int) Math.pow(2, this.maxDepth - this.level);
        int fullSquare = this.size / halfSquare;
        Color[][] colorGrid = new Color[size / fullSquare][size / fullSquare];
        this.ColorGrid(colorGrid, fullSquare);
        return colorGrid;
    }

    private void ColorGrid(Color[][] colorGrid, int fullSquare) {
        updateSizeAndPosition(size, xCoord, yCoord);
        if (this.children.length == 4)
        {
            this.children[0].ColorGrid(colorGrid, fullSquare);
            this.children[1].ColorGrid(colorGrid, fullSquare);
            this.children[2].ColorGrid(colorGrid, fullSquare);
            this.children[3].ColorGrid(colorGrid, fullSquare);

        } else
        {
            for (int i = 0; i < this.size / fullSquare; i++) // these are rows, it's the y coord
            {
                for (int j = 0; j < this.size / fullSquare; j++) // these are columns, it's the x coord
                {
                    colorGrid[this.yCoord / fullSquare + j][this.xCoord / fullSquare + i] = this.color;
                }
            }
        }
    }



    // These two get methods have been provided. Do NOT modify them.
    public int getMaxDepth() {
        return this.maxDepth;
    }

    public int getLevel() {
        return this.level;
    }


    /*
     * The next 5 methods are needed to get a text representation of a block.
     * You can use them for debugging. You can modify these methods if you wish.
     */
    public String toString() {
        return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
    }

    public void printBlock() {
        this.printBlockIndented(0);
    }

    private void printBlockIndented(int indentation) {
        String indent = "";
        for (int i=0; i<indentation; i++) {
            indent += "\t";
        }

        if (this.children.length == 0) {
            // it's a leaf. Print the color!
            String colorInfo = GameColors.colorToString(this.color) + ", ";
            System.out.println(indent + colorInfo + this);
        }
        else {
            System.out.println(indent + this);
            for (Block b : this.children)
                b.printBlockIndented(indentation + 1);
        }
    }

    private static void coloredPrint(String message, Color color) {
        System.out.print(GameColors.colorToANSIColor(color));
        System.out.print(message);
        System.out.print(GameColors.colorToANSIColor(Color.WHITE));
    }

    public void printColoredBlock(){
        Color[][] colorArray = this.flatten();
        for (Color[] colors : colorArray) {
            for (Color value : colors) {
                String colorName = GameColors.colorToString(value).toUpperCase();
                if(colorName.length() == 0){
                    colorName = "\u2588";
                }
                else{
                    colorName = colorName.substring(0, 1);
                }
                coloredPrint(colorName, value);
            }
            System.out.println();
        }
    }
}
