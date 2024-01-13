package compiler;
import java.util.Stack;

public class TestingParser
{
    public String input="";
    private int indexOfInput=-1;
    Stack <String> strack=new Stack<String>();
    String [][] table=
    {
        {"TA","TA",null,null,null,null,null,null},
        {null,null,"","+TB","-TB",null,null,""}  ,
        {"FB","FB",null,null,null,null,null,null}  ,
        {null,null,"","","","*FB","/FB",""}  ,
        {"i","(E)",null,null,null,null,null,null}  ,
    };
    String [] nonTers={"E","A","T","B","F"};
    String [] terminals={"i","(",")","+","-","*","/","$"};
 
//====================================================
public TestingParser(String in)
{
this.input=in;
}
//====================================================
private  void pushRule(String rule)
{
for(int i=rule.length()-1;i>=0;i--)
{
  char ch=rule.charAt(i);
  String str=String.valueOf(ch);
 
  push(str);
}
}
//====================================================
public String getRule(String non,String term)
{
        
    int row=getnonTermIndex(non);
    int column=getTermIndex(term);
    System.out.println(row + " "+column);
    String rule=this.table[row][column];
    System.out.println(rule);
    if(rule==null)
    {
       error("There is no Rule by this , Non-Terminal("+non+") ,Terminal("+term+") ");
    }
    return rule;
}
//====================================================
//parse
public void parse    ()
{
    push(this.input.charAt(0)+"");//
    push("E");
    //Read one token from input
    String token=read();
    System.out.println("token="+token);
    String top=null;
    do
    {
     top=this.pop();
     System.out.println("top="+top);
     //if top is non-terminal
     if(isNonTerminal(top))
     {
       String rule=this.getRule(top, token);
       this.pushRule(rule);
     }
     else if(isTerminal(top))
     {
       if(!top.equals(token))
       {
         error("this token is not corrent , By Grammer rule . Token : ("+token+")");
       }
       else
       {
         System.out.println("Matching: Terminal :( "+token+" )");
         token =read();
         //top=pop();
       }
      }
      else
      {
        error("Never Happens , Because top : ( "+top+" )");
      }
      if(token.equals("$"))
      {
        break;
      }
        //if top is terminal
    }while(true);//out of the loop when $
    
    //accept
    if(token.equals("$"))
    {
     System.out.println("Input is Accepted by LL1");   
    }
    else
    {
     System.out.println("Input is not Accepted by LL1");   
    }
}
//====================================================
private boolean isTerminal(String s)
{
  for(int i=0;i<this.terminals.length;i++)
  {
   if(s.equals(this.terminals[i]))
   {return true; }
  }
  return false;
}
//====================================================
private boolean isNonTerminal(String s)
{
  for(int i=0;i<this.nonTers.length;i++)
  {
     if(s.equals(this.nonTers[i]))
     {return true;}
  }
  return false;
}
//====================================================
private String read()
{
        indexOfInput++;
        char ch=this.input.charAt(indexOfInput);
        String str=String.valueOf(ch);
        return str;
}
//====================================================
private void push(String s)
{
     this.strack.push(s);   
}
//====================================================
private String pop()
{
   return this.strack.pop();   
}
//====================================================
private void error(String message)
{
        System.out.println(message);
        throw new RuntimeException(message);
}
//====================================================

 

//==================================================================
private int getnonTermIndex(String non)
{
       for(int i=0;i<this.nonTers.length;i++)
       {
        if(non.equals(this.nonTers[i]))
        {
         return i;
        }
        }
       error(non +" is not NonTerminal");
       return -1;
}
//====================================================
private int getTermIndex(String term)
{
  for(int i=0;i<this.terminals.length;i++)
  {
    if(term.equals(this.terminals[i]))
    {return i;}
  }
       error(term +" is not Terminal");
       return -1;
}
//====================================================
//main
public static void main(String[] args)
{
    TestingParser parser=new TestingParser("i*(i+i)*i$");
    parser.parse();   
    TestingParser parser2=new TestingParser("i+i*i/i$");
    parser2.parse();
}
 
}