function confirmDialog(msg) {
	return new Promise(function(resolve, reject) {
		const dialog = createHtmlTag({
			tagName : 'div',
			style:{ 'z-index': 6 },
			ch0 : {
				tagName : 'div',
				className : 'modal-backdrop', 
				style:{ 'background-color': 'rgba(166,166,166,0.5)' }
			},
			ch1 : {
				tagName : 'div',
				className : 'modal fade show',
				role : 'dialog',
				style : { display : 'block', 'text-align' : 'center', 'margin-top' : '30vh', },
				ch0 : {
					tagName : 'div',
					className : 'modal-dialog',
					role : "document",
					ch1 : {
						tagName : 'div',
						className : 'modal-content',
						ch1 : {
							tagName : 'div',
							className : 'modal-header',
							ch1 : {
								tagName : 'h5',
								className : 'modal-title',
								innerHTML : 'Confirmation',
								style:{margin:'auto'}
							},

						},
						ch2 : {
							tagName : 'div',
							className : 'modal-body',
							innerHTML : msg
						},
						ch3 : {
							tagName : 'div',
							className : 'modal-footer',
							ch1 : {
								style : {
									margin : 'auto',
									style:{width:'max-content'}
								},
								ch1:{
									tagName : 'button',
									innerHTML : 'Yes',
									className : 'btn btn-primary',
									style: {margin: '3px'},
									onclick : function(e) {
										resolve(true);
										dialog.parentNode.removeChild(dialog);
									}
								},
								ch2:{
									tagName : 'button',
									innerHTML : 'No',
									style: {margin: '3px'},
									className : 'btn btn-secondary',
									onclick : function(e) {
										resolve(false);
										dialog.parentNode.removeChild(dialog);
									}
								},
							}
						}
					}
				},

			}
		})

		document.body.prepend(dialog);
	});
}

function infoDialog(msg) {
	return new Promise(function(resolve, reject) {
		const dialog = createHtmlTag({
			tagName : 'div',
			style:{ 'z-index': 6 },
			ch0 : {
				tagName : 'div',
				className : 'modal-backdrop', 
				style:{ 'background-color': 'rgba(166,166,166,0.5)' }
			},
			ch1 : {
				tagName : 'div',
				className : 'modal fade show',
				role : 'dialog',
				style : { display : 'block', 'text-align' : 'center', 'margin-top' : '30vh', },
				ch0 : {
					tagName : 'div',
					className : 'modal-dialog',
					role : "document",
					ch1 : {
						tagName : 'div',
						className : 'modal-content',
						ch1 : {
							tagName : 'div',
							className : 'modal-header',
							ch1 : {
								tagName : 'h5',
								className : 'modal-title',
								innerHTML : 'Info',
								style:{margin:'auto'}
							},

						},
						ch2 : {
							tagName : 'div',
							className : 'modal-body',
							innerHTML : msg
						},
						ch3 : {
							tagName : 'div',
							className : 'modal-footer',
							ch1 : {
								style : {
									margin : 'auto'
								},
								tagName : 'button',
								innerHTML : 'Ok',
								className : 'btn btn-primary',
								onclick : function(e) {
									resolve(true);
									dialog.parentNode.removeChild(dialog);
								}
							}
						}
					}
				},

			}
		})

		document.body.prepend(dialog);
	});
}