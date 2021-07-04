module.exports = {
  getWebviewContent
}

function getWebviewContent() {
  html = `<!DOCTYPE html>
      <html lang="en">
      
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Linearizer</title>
        <style>
          body {
            position: relative;
            margin-top: 5%;
          }
      
          .form {
            display: flex;
            flex-direction: column;
            align-items: center;
            position: relative;
            height: 50%;
            width: 50%;
            
            margin: 0px auto;
            text-align: center;
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            margin: auto;
          }
      
          .inp {
            font-family: Roboto;
            font-size: 2vh;
            line-height: 5vh;
            width: 40vw;
            display: flex;
            align-items: center;
            background: #CCCCCC;
            border-color: transparent;
            color: #666666;
          }
      
          .buttons {
            display: flex;
            flex-direction: wrap;
            align-items: center;
          }
          .checks {
            margin-top: 1vh;
            display: flex;
            align-items: center;
          }

          .check{
            align: left;
            font-family: Roboto;
            font-style: normal;
            font-weight: normal;
            font-size: 2vh;
          }
      
          .func {
            border-radius: 5px;
            text-align: center;
            align-items: center;
            height: 8vh;
            width: 10vw;
            margin: 5%;
            font-family: Roboto;
            font-style: normal;
            font-weight: normal;
            font-size: 1;
            line-height: 1;
            text-align: center;
          }
      
          .func:not(.active) {
            background: #2631cd;
            color: #F7F7F7;
          }
      
          .func:hover {
            /* Цвет фона под ссылкой */
            background: #28cde3;
            color: #F7F7F7;
          }
      
          .func:active {
            background: #26138f;
            color: #F7F7F7;
          }

          .lines {
            font-family: Roboto;
            text-align: left;
          }
        </style>
      </head>
      
      <body>
        <div class="form">
            <h1>Git Linearizer</h1>
            <form class="lines">
                <p>Path to repository: <p>
                <input class="inp" type="text" placeholder="C:\\examplePackage\\...\\.git" id="rep">
                <p>Name of linearised branch: <p>
                <input class="inp" type="text" placeholder="name_of_the_branch" id="branch">
                <p>Start point: <p>
                <input class="inp" type="text" placeholder="ex12ample3456hash" id="start">
            </form>
            <div class="choice">
              <div class="checks">
                  <input type="checkbox" id="strip" />
                  <label class="check"> Strip commit messages</label>
              </div>
              <div class="checks">
                  <input type="checkbox" id="fix_case" />
                  <label class="check"> Fix case</label>
              </div>
              <div class="checks">
                  <input type="checkbox" id="remove" />
                  <label class="check"> Remove extra symbols</label>
              </div>
              <div class="checks">
                  <input type="checkbox" id="fix_messages" />
                  <label class="check"> Fix big messages</label>
              </div>
            </div>
            <div class="buttons">
                <button class="func" id="button_linearize">
                    Linearize
                </button>
                <button class="func" id="button_help">
                    Help
                </button>
            </div>
        </div>
        <script>
          const vscode = acquireVsCodeApi();
          const buttonHelp = document.getElementById('button_help');
          const buttonLinearize = document.getElementById('button_linearize');

          buttonHelp.addEventListener('click', helpFunc);
          buttonLinearize.addEventListener('click', linFunc);
          
          function helpFunc() {
            vscode.postMessage({
              command: 'help'
            }
            )
          };
          function linFunc() {
            vscode.postMessage({
              command: 'linearize',
              rep: document.getElementById('rep').value,
              start: document.getElementById('start').value,
              branch: document.getElementById('branch').value,
              strip: document.getElementById('strip').checked,
              fix_case: document.getElementById('fix_case').checked,
              remove: document.getElementById('remove').checked,
              fix_messages: document.getElementById('fix_messages').checked
            }
            )
          };
        </script>
      </body>
      
      </html>`;

  return html;
}