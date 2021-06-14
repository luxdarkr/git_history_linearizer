const vscode = require('vscode');
const { getWebviewContent } = require('./webviewContent')
const { helpFunc, hello, help, linearize, linFunc, fixFunc } = require('./functions')

/**
 * @param {vscode.ExtensionContext} context
 */

function activate(context) {

  let greeting = vscode.commands.registerCommand('lin.hello', hello);
  let helpLin = vscode.commands.registerCommand('lin.help', help);
  let linearizeLin = vscode.commands.registerCommand('lin.linearize', linearize);

  context.subscriptions.push(greeting);
  context.subscriptions.push(helpLin);
  context.subscriptions.push(linearizeLin);
  context.subscriptions.push(
    vscode.commands.registerCommand('lin.start', () => {
      // Create and show panel
      const panel = vscode.window.createWebviewPanel(
        'lin',
        'Linearizer',
        vscode.ViewColumn.One,
        {
          enableScripts: true
        }
      );

      // And set its HTML content
      panel.webview.html = getWebviewContent();

      // Handle messages from the webview
      panel.webview.onDidReceiveMessage(
        message => {
          switch (message.command) {
            case 'help':
              helpFunc();
              return;
            case 'linearize':
              linFunc(message.rep, message.start, message.branch);
              return;
            case 'fix':
              fixFunc(message.rep, message.start, message.branch);
              return;
          }
        },
        undefined,
        context.subscriptions
      );
    })
  );
}

exports.activate = activate;

function deactivate() {
}

