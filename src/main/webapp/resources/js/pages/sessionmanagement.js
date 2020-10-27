function createToggleChatButton(code, count) {
	const buttonToggleChat = createHtmlTag({
		tagName : 'button',
		className : 'btn btn-secondary',
		id : 'do-toggle-msg' + code,
		innerHTML : "Toggle Chat(" + count + ")",
		onclick: function(e){
			var display = "block";
			if (byId("chat-msg-" + response.code).style.display == "block") {
				display = "none";
			}
			byId("chat-msg-" + response.code).style.display = display;
		}
	});
	return buttonToggleChat;
}

function createMessageHtmlContent(message) {
	return createHtmlTag({
		tagName : 'div',
		ch0 : {
			tagName : 'h4',
			innerHTML : (message.admin == 1 ? 'ADMIN' : "USER " + alias)
		},
		ch1 : {
			tagName : 'span',
			style : {
				'font-size' : '0.7em'
			},
			innerHTML : 'at ' + message.date
		},
		ch2 : {
			tagName : 'div',
			className : (message.admin == 1 ? 'chat-item-admin'
					: 'chat-item-client'),
			innerHTML : message.text
		}
	});
}


function createInputMessageHtml(code){
	const cell = createHtmlTag({
		tagName:'td',
		colspan: 2,
		ch0: {
			tagName:'input',
			className:'form-control',
			type:'text',
			id:'reply-msg'+code,
			placeholder:'reply',
		},
		ch1: {
			tagName: 'button',
			className: 'btn btn-success',
			id: 'do-reply-msg'+code,
			innerHTML: "<i class=\"fas fa-paper-plane\"></i>",
			onclick: function(e) {
				const messageContent = byId("reply-msg"+ response.code).value;
				sendReply(response.code, messageContent);
			}
		}
	});
	const row = document.createElement("tr");
	row.appendChild(cell);
	 
	return row;
}