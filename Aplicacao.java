public class Aplicacao
{
    public static void main(String[] args) {
        BTree b = new BTree();
        for (int i=1; i<100 ; i++)
            b.inserir(i,  0);

        System.out.println("------------------");
        b.in_ordem();

        for (int i=10; i<80 ; i++)
            b.exclusao(i);

        System.out.println("------------------");
        b.in_ordem();


    }
}
