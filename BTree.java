public class BTree implements Constantes
{
    private No raiz;

    public BTree()
    {
        raiz = null;
    }

    private No navegarAteFolha(int info)
    {
        int pos;
        No no=raiz;
        while(no.getvLig(0) != null)
        {
            pos = no.procurarPosicao(info);
            no = no.getvLig(pos);
        }
        return no;
    }

    private No localizarPai(No folha, int info)
    {
        int pos;
        No no=raiz, pai=raiz;
        while(no!=folha)
        {
            pai=no;
            pos = no.procurarPosicao(info);
            no = no.getvLig(pos);
        }
        return pai;
    }

    private void split(No folha, No pai)
    {
        No cx1 = new No();
        No cx2 = new No();
        for(int i=0; i<m; i++)
        {
            cx1.setvInfo(i, folha.getvInfo(i));
            cx1.setvPos(i, folha.getvPos(i));
            cx1.setvLig(i, folha.getvLig(i));
        }
        cx1.setvLig(m, folha.getvLig(m));
        cx1.setTl(m);

        for(int i=m+1; i<2*m+1; i++)
        {
            cx2.setvInfo(i-(m+1), folha.getvInfo(i));
            cx2.setvPos(i-(m+1), folha.getvPos(i));
            cx2.setvLig(i-(m+1), folha.getvLig(i));
        }
        cx2.setvLig(m, folha.getvLig(2*m+1));
        cx2.setTl(m);
        if(folha == pai)
        {
            folha.setvInfo(0, folha.getvInfo(m));
            folha.setvPos(0, folha.getvPos(m));
            folha.setvLig(0, cx1);
            folha.setvLig(1, cx2);
            folha.setTl(1);
        }
        else
        {
            int pos = pai.procurarPosicao(folha.getvInfo(m));
            pai.remanejar(pos);
            pai.setvInfo(pos, folha.getvInfo(m));
            pai.setvPos(pos, folha.getvPos(m));
            pai.setTl(pai.getTl() + 1);
            pai.setvLig(pos, cx1);
            pai.setvLig(pos+1, cx2);
            if (pai.getTl() > 2*m)
            {
                folha = pai;
                pai = localizarPai(folha, folha.getvInfo(0));
                split(folha, pai);
            }
        }
    }

    public void inserir(int info, int posArq)
    {
        No folha, pai;
        if (raiz == null)
            raiz = new No(info, posArq);
        else
        {
            folha = navegarAteFolha(info);
            int pos = folha.procurarPosicao(info);
            folha.remanejar(pos);
            folha.setvInfo(pos, info);
            folha.setvPos(pos, posArq);
            folha.setTl(folha.getTl()+1);
            if(folha.getTl() > 2*m)
            {
                pai = localizarPai(folha, info);
                split(folha, pai);
            }
        }
    }

    //--- exclusão ---
    private No localizarNo(int info)
    {
        No no=raiz;
        boolean achou=false;
        while(no!=null && !achou)
        {
            int pos=no.procurarPosicao(info);
            if(pos<no.getTl() && no.getvInfo(pos)==info)
                achou=true;
            else
                no = no.getvLig(pos);
        }
        return no;
    }

    private No localizarSubE(No no, int pos)
    {
        no=no.getvLig(pos);
        while(no.getvLig(0)!=null)
            no=no.getvLig(no.getTl());
        return no;
    }

    private No localizarSubD(No no, int pos)
    {
        no=no.getvLig(pos);
        while(no.getvLig(0)!=null)
            no=no.getvLig(0);
        return no;
    }

    public void exclusao(int info)
    {
        No subE,subD,folha;
        No no = localizarNo(info);
        if (no!=null)
        {
            int pos = no.procurarPosicao(info);
            if (no.getvLig(0)!=null) //não é folha
            {
                subE = localizarSubE(no, pos);
                subD = localizarSubD(no, pos+1);
                if(subE.getTl() >m || subD.getTl()==m)
                {
                    no.setvInfo(pos, subE.getvInfo(subE.getTl()-1));
                    no.setvPos(pos, subE.getvPos(subE.getTl()-1));
                    folha=subE;
                    pos=subE.getTl()-1;
                }
                else
                {
                    no.setvInfo(pos, subD.getvInfo(0));
                    no.setvPos(pos, subD.getvPos(0));
                    folha=subD;
                    pos=0;
                }
            }
            else
                folha=no;

            folha.remanejarExclusao(pos);
            folha.setTl(folha.getTl()-1);

            if(folha==raiz && folha.getTl()==0)
                raiz=null;
            else
            if(folha!=raiz && folha.getTl()<m)
                redistribui_concatena(folha);
        }
    }

    private void redistribui_concatena(No folha)
    {
        No pai = localizarPai(folha, folha.getvInfo(0));
        No irmaE=null, irmaD=null;
        int posPai = pai.procurarPosicao(folha.getvInfo(0));
        if(posPai>0)
            irmaE = pai.getvLig(posPai-1);
        if(posPai<pai.getTl())
            irmaD = pai.getvLig(posPai+1);

        //redistribuição com a irmaE
        if (irmaE!=null && irmaE.getTl()>m)
        {
            folha.remanejar(0);
            folha.setvInfo(0, pai.getvInfo(posPai-1));
            folha.setvPos(0, pai.getvPos(posPai-1));
            folha.setTl(folha.getTl()+1);
            pai.setvInfo(posPai-1, irmaE.getvInfo(irmaE.getTl()-1));
            pai.setvPos(posPai-1, irmaE.getvPos(irmaE.getTl()-1));
            folha.setvLig(0, irmaE.getvLig(irmaE.getTl()));
            irmaE.setTl(irmaE.getTl()-1);
        }
        else
        //redistribuição com a irmaD
        if (irmaD!=null && irmaD.getTl()>m)
        {   
            folha.setvInfo(folha.getTl(), pai.getvInfo(posPai));
            folha.setvPos(folha.getTl(), pai.getvPos(posPai));
            folha.setTl(folha.getTl()+1);
            folha.setvLig(folha.getTl(), irmaD.getvLig(0));
            pai.setvInfo(posPai, irmaD.getvInfo(0));
            pai.setvPos(posPai, irmaD.getvPos(0));
            irmaD.remanejarExclusao(0);
            irmaD.setTl(irmaD.getTl()-1);
        }
        else
        {
            //concatenação
            if(irmaE!=null)
            {
                irmaE.setvInfo(irmaE.getTl(), pai.getvInfo(posPai-1));
                irmaE.setvPos(irmaE.getTl(), pai.getvPos(posPai-1));
                irmaE.setTl(irmaE.getTl()+1);
                pai.remanejarExclusao(posPai-1);
                pai.setTl(pai.getTl()-1);
                for(int i=0; i<folha.getTl(); i++)
                {
                    irmaE.setvInfo(irmaE.getTl(), folha.getvInfo(i));
                    irmaE.setvPos(irmaE.getTl(), folha.getvPos(i));
                    irmaE.setvLig(irmaE.getTl(), folha.getvLig(i));
                    irmaE.setTl(irmaE.getTl()+1);
                }
                irmaE.setvLig(irmaE.getTl(), folha.getvLig(folha.getTl()));
                pai.setvLig(posPai-1, irmaE);
            }
            else //irmaD
            {
                irmaD.remanejar(0);
                irmaD.setvInfo(0, pai.getvInfo(posPai));
                irmaD.setvPos(0, pai.getvPos(posPai));
                irmaD.setTl(irmaD.getTl()+1);
                pai.remanejarExclusao(posPai);
                pai.setTl(pai.getTl()-1);
                irmaD.setvLig(0, folha.getvLig(folha.getTl()));
                for(int i = folha.getTl()-1; i>=0; i--)
                {
                    irmaD.remanejar(0);
                    irmaD.setvInfo(0, folha.getvInfo(i));
                    irmaD.setvPos(0, folha.getvPos(i));
                    irmaD.setvLig(0, folha.getvLig(i));
                    irmaD.setTl(irmaD.getTl()+1);
                }
                
            }

            if(pai.getTl()==0)
            {
                if(irmaE!=null)
                    raiz=irmaE;
                else
                    raiz=irmaD;
            }
            else
            if(pai!=raiz && pai.getTl()<m)
            {
                folha=pai;
                redistribui_concatena(folha);
            }
        }
    }

    //----------------
    public void in_ordem()
    {
        in_ordem(raiz);
    }

    private void in_ordem(No raiz)
    {
        if(raiz!=null)
        {
            for(int i=0; i<raiz.getTl() ; i++)
            {
                in_ordem(raiz.getvLig(i));
                System.out.println(raiz.getvInfo(i));
            }
            in_ordem(raiz.getvLig(raiz.getTl()));
        }
    }
}
