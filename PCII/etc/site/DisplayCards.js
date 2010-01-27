

var newWin;

function displayCard()
{
var newdoc=arguments[0];
//alert(arguments[1]+" / "+arguments[2]);
var unitIndex = arguments[1];
var unit = combatData[unitIndex][0];
var unitPlusNumber=unit;
if(arguments[2]>0) unitPlusNumber+=" ("+(arguments[2]+1)+")";
var country = combatData[unitIndex][5];
var flag = country+"Flag.gif";
//replace space and slash by underscore
var pattern=new RegExp(" |\/","g");
var unitImage="UnitImages/"+unit.replace(pattern,"_")+".jpg";
var fontTag="<font style=\"font-family:arial; color:#000000; font-size:12pt\">";
newdoc.write("<table border=0 background=crumple.jpg height=296 width=208>");
newdoc.writeln("<TR><TD colspan=2 align=left><img src="+flag+" height=20 width=35></TD></TR>");
newdoc.writeln("<TR><td align=middle colspan=2>"+fontTag+"<b>"+unitPlusNumber+"</b></font></td></TR>");
newdoc.writeln("<tr><td align=middle colspan=2><img height=90 src="+unitImage+"></td></tr>");
newdoc.writeln("<TR><TD colspan=2 align=center>");
newdoc.writeln("<table border=0>");
newdoc.writeln("<tr><td>"+fontTag+"Move</font></td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>");
newdoc.writeln("<td>"+fontTag+combatData[unitIndex][1]+"</font></td></tr>");
newdoc.writeln("<tr><td>"+fontTag+"AP</font></td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>");
newdoc.writeln("<td>"+fontTag+combatData[unitIndex][2]+"</font></td></tr>");
newdoc.writeln("<tr><td>"+fontTag+"AT</font></td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>");
newdoc.writeln("<td>"+fontTag+combatData[unitIndex][3]+"</font></td></tr>");
newdoc.writeln("<tr><td>"+fontTag+"Defend</font></td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>");
newdoc.writeln("<td>"+fontTag+combatData[unitIndex][4]+"</font></td></tr>");
newdoc.writeln("</table>");
newdoc.writeln("</table>");
}

function displayCards()
{

//construct array of units to display first as the document will change
var numberOfUnitsOfType=new Array;
var numCards=0;
var iElement=0;
for(i=0;i<combatData.length;i++)
{
  while(document.unitSelectionForm.elements[iElement].type!="select-one") iElement++;
  var numberOfUnitsOfThisType=parseInt(document.unitSelectionForm.elements[iElement].value);
  iElement++;
  numberOfUnitsOfType[i]=numberOfUnitsOfThisType;
  numCards+=numberOfUnitsOfThisType;
}
if(numCards==0)
{
  alert("First select your units!"); 
  return false;
}

if(!confirm("Do you want to create "+numCards+" cards?")) return false;

var myBars = "directories=no,location=no,menubar=yes,status=no,titlebar=no,toolbar=no";
var myOptions = "scrollbars=yes,width=700,height=600,resizeable=yes";
var myFeatures = myBars + "," + myOptions;
newWin = open("", "Cards", myFeatures);
newWin.document.writeln("<html><body BACKGROUND=BackgroundGrey.jpg><bgsound src=\"sounds\/whoosh.wav\"><center>");
newWin.document.writeln("<table border=2><tr>");
numCards=0;
var numRows=0;
var numCardsPerCol=3;
var numRowsPerPage=3;
var numPages=1;
for(i=0;i<combatData.length;i++)
{ 
  var numberOfUnitsOfThisType=numberOfUnitsOfType[i];
  for(j=0;j<numberOfUnitsOfThisType;j++)
  {  
     newWin.document.writeln("<td>");
     displayCard(newWin.document,i,j);
     newWin.document.writeln("</td>");
     numCards++;
     if(numCards%numCardsPerCol==0)
     { 
        numRows++;
        if(numRows%numRowsPerPage==0)
        {
           newWin.document.writeln("</tr></table><table style=page-break-before:always; border=2><tr>");
           numPages++;
        }
        else newWin.document.writeln("</tr><tr>");
     }
  }
}
newWin.document.writeln("</tr></table></center></body></html>");
newWin.document.close();
for(i=0;i<250;i++) newWin.scrollBy(0,1);
if(!newWin.confirm("Do you want to print these "+numPages+" pages?")) return false;
newWin.print();
return false;
}

