

function addOrRemoveUnits()
{
  var iElement=0;
  for(i=0;i<combatData.length;i++)
  {
    while(document.unitSelectionForm.elements[iElement].type!="select-one")
    { 		
      iElement++;
    } 
    if(combatData[i][5]==arguments[1])
    {
      var numberOfUnitsOfThisType=parseInt(document.unitSelectionForm.elements[iElement].value);
      if(arguments[0].value=="+1") numberOfUnitsOfThisType++; 
      else numberOfUnitsOfThisType--;
      if(numberOfUnitsOfThisType>10) numberOfUnitsOfThisType=10;
      if(numberOfUnitsOfThisType<0) numberOfUnitsOfThisType=0;
      document.unitSelectionForm.elements[iElement].value=numberOfUnitsOfThisType;
    }
    iElement++;
  }
}
