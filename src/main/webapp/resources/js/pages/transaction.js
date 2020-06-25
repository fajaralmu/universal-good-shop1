var ENTITY_GET_URL;
function receiptFooterRow(summaryPrice) {
	return createHtmlTag({
		"tagName" : "tr",
		"ch1" : {
			"tagName" : "td",
			"style" : {
				"text-align" : "right"
			},
			"colspan" : 7,
			"innerHTML" : "Total : " + beautifyNominal(summaryPrice)
		}
	});
}

function receiptHeaderRow2() {
	return createHtmlTag({
		"tagName" : "tr",
		"ch1" : {
			"tagName" : "td",
			"style" : {
				"text-align" : "center"
			},
			"colspan" : 7,
			"ch1" : {
				"tagName" : "h3",
				"innerHTML" : "Products"
			}
		}
	});
}

function receiptHeaderRow(summaryPrice) {
	return createHtmlTag({
		tagName : 'tr',
		ch1 : {
			tagName : 'td',
			innerHTML : 'Transaction Amount'
		},
		ch2 : {
			tagName : 'td',
			style : {
				'text-align' : 'left'
			},
			colspan : 2,
			ch1 : {
				tagName : 'u',
				innerHTML : beautifyNominal(summaryPrice)
			}
		}
	});
}


function loadStakeHolderList(entityDropDown, entityName, entityFieldName, filterValue, onOptionClick) {
	if(ENTITY_GET_URL == null){
		alert("ENTITY_GET_URL not defined!");
	}
	
	clearElement(entityDropDown);
	var requestObject = {
		"entity" : entityName,
		"filter" : {
			"page" : 0,
			"limit" : 10
		}
	};
	requestObject.filter.fieldsFilter = {};
	requestObject.filter.fieldsFilter[entityFieldName] = filterValue;

	loadEntityList(
			ENTITY_GET_URL,
			requestObject,
			function(entities) {
				for (let i = 0; i < entities.length; i++) {
					const entity = entities[i];
					const option = createHtmlTag({
						tagName: 'option',
						value: entity["id"],
						innerHTML:  entity[entityFieldName],
						onclick :  function(entity) {
							onOptionClick(entity);
						}
					});
					entityDropDown.append(option);
				}
			});
}