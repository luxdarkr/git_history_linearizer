const vscode = require('vscode');
const { getWebviewContent } = require('./webviewContent')
const { helpFunc, hello, linFunc } = require('./functions')

/**
 * @param {vscode.ExtensionContext} context
 */

function activate(context) {

  let greeting = vscode.commands.registerCommand('lin.hello', hello);
  let help = vscode.commands.registerCommand('lin.help', helpFunc);

  context.subscriptions.push(greeting);
  context.subscriptions.push(help);
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
              linFunc(message.rep,
                message.start,
                message.branch,
                message.strip,
                message.fix_case,
                message.remove,
                message.fix_messages);
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

