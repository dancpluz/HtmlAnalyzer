package HtmlAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class HtmlAnalyzer {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String result = analyzeHTML(reader.readLine());
        System.out.println(result);
    }

    public static String analyzeHTML(String urlStr) {
    try {
        // Tenta extrair o código HTML do link dado
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(1000); // Timeouts para evitar erros de conexão
        connection.setReadTimeout(1000);

        // BufferedReader para ler cada linha do código
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        // Pilha de Árvores para gerar a Árvore completa
        Stack<TreeNode> stack = new Stack<>();
        // Pilha de tags anteriores para identificar erros de código html
        Stack<String> pastTags = new Stack<>();
        
        // Definição da raiz da árvore
        pastTags.push("html");
        TreeNode root = new TreeNode(reader.readLine());
        
        // Loop em todas as linhas do html
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            int j = line.indexOf('<');
            
            // Caso a linha não tenha uma tag, adiciona com filho da raiz
            if (j == -1) {
                root.children.add(new TreeNode(line));
                continue;
            }
            
            String fullTag = line;
            // Formata a tag se for de abertura ou fechamento
            boolean closeTag = fullTag.charAt(1) == '/';
            String tag = closeTag ? fullTag.substring(2,fullTag.length()-1) : fullTag.substring(1,fullTag.length()-1);      
            
            
            if (!closeTag) {
                pastTags.push(tag);
                
                // Caso a tag seja de abertura adiciona na pilha de árvores e cria uma nova raiz com a tag atual
                stack.push(root);
                root = new TreeNode(fullTag);
            } else {
                
                if (!pastTags.peek().equals(tag)) return "malformed HTML";
                pastTags.pop();
                if (stack.isEmpty()) break;
                
                // Caso a tag seja de fechamento, adiciona a raiz atual como filha da árvore do topo e muda a raiz para ela
                stack.peek().children.add(root);
                root = stack.peek();
                stack.pop();
            }
        }
        reader.close();
        
        if (!pastTags.isEmpty()) return "malformed HTML";
        return root.deepest().tagName;
        
    // Caso não seja possível conectar
    } catch (Exception e) {
        //e.printStackTrace(); // Por motivos do teste, os erros foram omitidos
        return "URL connection error"; 
        }
    }
}

// Estrutura simplificada de árvore não binária com a tag/texto da linha de html e um lista de filhos
class TreeNode{
        String tagName;
        ArrayList<TreeNode> children = new ArrayList<TreeNode>();

        TreeNode(String data){
            tagName = data;
        }
        
    // Função que retorna o ramo mais profundo da Árvore
    public TreeNode deepest() {
        // Listas para guardar a maior profundidade vista e a Árvore mais profunda
        int[] maxDepth = new int[]{0};
        TreeNode[] deepestNode = new TreeNode[]{null};
        
        // Chama a função recursiva para a raiz da Árvore, profundidade 0
        findDeepest(this, 0, maxDepth, deepestNode);
        return deepestNode[0];
    }
    
    // Função recursiva para passar pela Árvore e verificar a maior profundidade
    private void findDeepest(TreeNode node, int depth, int[] maxDepth, TreeNode[] deepestNode) {
        // Se o Node atual for mais profundo já visto, atualize
        if (depth > maxDepth[0]) {
            maxDepth[0] = depth;
            deepestNode[0] = node;
        }
        
        // Chamada recursiva para todos os filhos do Node
        for (TreeNode child : node.children) {
            findDeepest(child, depth + 1, maxDepth, deepestNode);
        }
    }
} 