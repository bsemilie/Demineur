import javax.swing.*;
import java.util.Random;


/**
 * Classe pour un champ de démineur
 */
public class Champ {

    public final static int DIMX_DEF=10; //Dimension X par default
    public final static int DIMY_DEF=10; //Dimension Y par défault
    private final static int NB_MINES_DEF=5; //Nombre de mines par défault
    private final static int MINE = -1; //Valeur en entier d'une mine
    public int[][] champ; //Tableau de bool



    public static int dimX; // Dimension X du champ
    public static int dimY; //Dimension Y du champ



    public static int nbMines; //Nombre de Mines dans le champ
    final static int[] easyLevel = {10,10,16}; //Paramètres du niveau facile
    final static int[] mediumLevel= {20,20,63}; //Paramètres du niveau medium
    final static int[] hardLevel = {30, 30, 141}; //Paramètres du niveau difficile
    public int[] customLevel = new int[3]; //Paramètres du niveau custom


    Random aleaGenerator = new Random(); //générateur d'aléatoire

    /**
     * Constructeur par défaut
     */
    public Champ(){
       this(Common.Niveau.EASY);
    }

    /**
     * Constructeur de champ en fonction du niveau choisi
     * @param niveau niveau de jeu
     */
    public Champ(Common.Niveau niveau){
        switch (niveau) {
            case EASY -> initChamp(easyLevel[0], easyLevel[1], easyLevel[2]);
            case MEDIUM -> initChamp(mediumLevel[0], mediumLevel[1], mediumLevel[2]);
            case HARD -> initChamp(hardLevel[0], hardLevel[1], hardLevel[2]);
            case CUSTOM -> initChamp(customLevel[0], customLevel[1], customLevel[2]);
            default -> throw new IllegalStateException("Unexpected value: " + niveau);
        }
    }

    public void setLevel(Common.Niveau niveau){
        switch (niveau) {
            case EASY -> initChamp(easyLevel[0], easyLevel[1], easyLevel[2]);
            case MEDIUM -> initChamp(mediumLevel[0], mediumLevel[1], mediumLevel[2]);
            case HARD -> initChamp(hardLevel[0], hardLevel[1], hardLevel[2]);
            case CUSTOM -> initChamp(customLevel[0], customLevel[1], customLevel[2]);
            default -> throw new IllegalStateException("Unexpected value: " + niveau);
        }
    }

    /**
     * Fonction d'initialisation d'un champ avec des paramètres en arguments
     * @param Xsize dimension X du champ
     * @param Ysize dimension Y du champ
     * @param nb_Mines nombre de mines du champ
     */
    public void initChamp(int Xsize, int Ysize, int nb_Mines)
    {
        dimX = Xsize;
        dimY = Ysize;
        nbMines = nb_Mines;
        this.champ = new int[dimX][dimY];
        placeMines();
        setNbMinesCase();
    }



    /**
     * Fonction pour placer aléatoirement des mines dans un champ donné
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
     * Fonction pour afficher un champ avec Mines et nombre de mines autour
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
     * Affichage du champ en string
     * @return
     */
    public String toString(){
        affChamp();
        return ("");
    }

    /**
     * Fonction pour calculer le nombre de mines autour d'une case
     * @param X position X de la case
     * @param Y position Y de la case
     * @return nombre de mines
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
     * Fonction pour mettre le nombre de mines autour d'une case dans la case
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

    public static int getNbMines() {
        return nbMines;
    }

    public static int getDimX() {
        return dimX;
    }

    public static int getDimY() {
        return dimY;
    }
}


