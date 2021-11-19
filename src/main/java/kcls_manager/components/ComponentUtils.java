package kcls_manager.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

public class ComponentUtils
{
    public static Dimension 
    toDimensionFromEM( Component comp, float xEM, float yEM )
    {
        float   fontSize    = comp.getFont().getSize2D();
        int     xPX         = round( xEM * fontSize );
        int     yPX         = round( yEM * fontSize );
        Dimension   dim = new Dimension( xPX, yPX );
        return dim;
    }
    
    public static float cvtFromEM( Component comp, float ems )
    {
        float   fontSize    = comp.getFont().getSize2D();
        float   pxs         = ems * fontSize;
        return pxs;
    }
    
    public static float cvtFromEM( float fontSize, float ems )
    {
        float   pxs = ems * fontSize;
        return pxs;
    }

    public static Insets toInsetsFromEM( Component comp, float ems )
    {
        
        Insets  insets  = 
            toInsetsFromEM( comp, ems, ems, ems, ems );
        return insets;
    }

    public static Insets toInsetsFromEM(
        Component comp, 
        float topEM, 
        float leftEM
    )
    {
        Insets  insets  = 
            toInsetsFromEM( comp, topEM, leftEM, topEM, leftEM );
        return insets;
    }

    public static Insets toInsetsFromEM(
        Component comp, 
        float topEM, 
        float leftEM, 
        float bottomEM, 
        float rightEM
    )
    {
        float   fontSize    = comp.getFont().getSize2D();
        int     topPX       = round( topEM * fontSize );
        int     leftPX      = round( leftEM * fontSize );
        int     bottomPX    = round( bottomEM * fontSize );
        int     rightPX     = round( rightEM * fontSize );
        Insets  insets      = new Insets( topPX, leftPX, bottomPX, rightPX );
        return insets;
    }
    
    public static int round( double dVal )
    {
        int iVal    = (int)(dVal + .5);
        return iVal;
    }
    
    public static class FloatDimension
    {
        /**
         * @return the xDim
         */
        public float getxDim()
        {
            return xDim;
        }

        /**
         * @param xDim the xDim to set
         */
        public void setxDim(float xDim)
        {
            this.xDim = xDim;
        }

        /**
         * @return the yDim
         */
        public float getyDim()
        {
            return yDim;
        }

        /**
         * @param yDim the yDim to set
         */
        public void setyDim(float yDim)
        {
            this.yDim = yDim;
        }

        private float   xDim;
        private float   yDim;
        
        public FloatDimension( float xDim, float yDim )
        {
            this.xDim = xDim;
            this.yDim = yDim;
        }
    }
}
