package game.demineur;
import java.util.Random;
import java.util.zip.CheckedOutputStream;


/**
 * This class is a field for a demineur instance
 */
public class Champ {

    private final static int MINE = -1; //Valeur en entier d'une mine
    private Common.Niveau niveau; //Level of the demineur game
    public int[][] champ; //Int array to represent the field

    public static int dimX; // Dimension X du champ
    public static int dimY; //Dimension Y du champ
    public static int nbMines; //Nombre de Mines dans le champ

    final static int[] easyLevel = {10,10,16}; //Paramètres du niveau facile
    final static int[] mediumLevel= {20,20,63}; //Paramètres du niveau medium
    final static int[] hardLevel = {30, 30, 141}; //Paramètres du niveau difficile
    public int[] customLevel = new int[3]; //Paramètres du niveau custom


    Random aleaGenerator = new Random(); //générateur d'aléatoire

    /**
     * Constructor
     */
    public Champ(){

    }

    /**
     * Creates a new champ instance depending on difficulty
     * @param niveau of game chosen
     */
    public Champ(Common.Niveau niveau){
       this();
       setChamp(niveau);
    }


    /**
     * This function set level for the field and init with dimension parameters
     * @param niveau of difficulty chosen
     */
    public void setChamp(Common.Niveau niveau){
        if(niveau == Common.Niveau.EASY){
            this.niveau = niveau;
            this.dimX = easyLevel[0];
            this.dimY = easyLevel[1];
            this.nbMines = easyLevel[2];
            this.champ = new int[dimX][dimY];
        }
        else if(niveau == Common.Niveau.MEDIUM){
            this.niveau = niveau;
            this.dimX = mediumLevel[0];
            this.dimY = mediumLevel[1];
            this.nbMines = mediumLevel[2];
            this.champ = new int[dimX][dimY];
        }
        else if(niveau == Common.Niveau.HARD){
            this.niveau = niveau;
            this.dimX = hardLevel[0];
            this.dimY = hardLevel[1];
            this.nbMines = hardLevel[2];
            this.champ = new int[dimX][dimY];
        }
        else if(niveau == Common.Niveau.CUSTOM){
            this.niveau = niveau;
            this.dimX = customLevel[0];
            this.dimY = customLevel[1];
            this.nbMines = customLevel[2];
            this.champ = new int[dimX][dimY];
        }
    }


    /**
     * This function place randomly mines in the field
     */
    public void placeMines(){
        for (int minesCounter=0; minesCounter<nbMines; minesCounter++){
            /* Generation position random*/
            int posX = aleaGenerator.nextInt(dimX);
            int posY= aleaGenerator.nextInt(dimY);

            /*! Assert mines doesn't already exists*/
            while(this.champ[posX][posY] == -1){
                posX = aleaGenerator.nextInt(dimX);
                posY= aleaGenerator.nextInt(dimY);
           }
            this.champ[posX][posY]= MINE; //Placement de la mine
        }

    }

    /**
     * THis function display a field with mines and nb of mines around each case
     */
    public void affChamp(){
        System.out.println("VOICI LE CHAMP");
        for(int XCounter=0; XCounter<dimX; XCounter++)
        {
            for(int YCounter=0; YCounter<dimY; YCounter++)
            {
                /* If mines then display X*/
                if(this.champ[XCounter][YCounter] == -1)
                {
                    System.out.print("X");
                }
                else{
                    System.out.print(this.champ[XCounter][YCounter]); /* Empty case*/
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    /**
     * This function display a filed in string format
     * @return
     */
    public String toString(){
        affChamp();
        return ("");
    }

    /**
     * This function calculates the nb of mines aorund the selected case and returns it
     * @param X  x-coordinate of the case
     * @param Y y-coordinate of the case
     * @return
     */
    public int calculNbMines(int X, int Y){
        int NbMines =0;
        for(int _i=-1; _i<2; _i++)
        {
            for(int _j=-1; _j<2; _j++)
            {
                int posX= X+_i;
                int posY = Y +_j;
                if((posX==-1)||(posY==-1)||((posX==X)&&(posY==Y)) || (posX>=dimX) || (posY>=dimY))
                {
                    continue;
                }
                if(this.champ[posX][posY] == MINE)
                {
                    NbMines++;
                }
            }
        }
        return NbMines;
    }

    /**
     * This function set teh case value with nb of mines around
     */
    public void setNbMinesCase()
    {
        for(int XCounter=0; XCounter<dimX; XCounter++)
        {
            for(int YCounter=0; YCounter<dimY; YCounter++)
            {
                if(this.champ[XCounter][YCounter] == MINE)
                {
                    continue;
                }
                else
                {
                    this.champ[XCounter][YCounter]= calculNbMines(XCounter, YCounter);
                }
            }
        }
    }

    /**
     * This function returns the level of the field
     * @return
     */
    public Common.Niveau getNiveauChamp()
    {
        if(dimX == easyLevel[0] && dimY == easyLevel[1] && nbMines == easyLevel[2])
        {
            return Common.Niveau.EASY;
        }
        else if(dimX == mediumLevel[0] && dimY == mediumLevel[1] && nbMines == mediumLevel[2])
        {
            return Common.Niveau.MEDIUM ;
        }
        else if(dimX == hardLevel[0] && dimY == hardLevel[1] && nbMines== hardLevel[2])
        {
            return  Common.Niveau.HARD;
        }
        else
        {
            return Common.Niveau.CUSTOM;
        }
    }

    /**
     * This function returns the nb of mines in the field
     * @return
     */
    public static int getNbMines() {
        return nbMines;
    }

    /**
     * This function returns the x dimension of the field
     * @return
     */
    public static int getDimX() {
        return dimX;
    }

    /**
     * This function returns the y dimension of the field
     * @return
     */
    public static int getDimY() {
        return dimY;
    }

    /**
     * This function returns the board of the field where case value are stored
     * @return
     */
    public int[][] getBoard() {
        return champ;
    }
}


