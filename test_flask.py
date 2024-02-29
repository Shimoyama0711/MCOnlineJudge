# Flaskライブラリをインポートします
from flask import Flask

# Flaskアプリケーションのインスタンスを作成します
app = Flask(__name__)


# ルートURL ("/") にアクセスがあった場合に実行される関数を定義します
@app.route("/")
def index():
    # ユーザーに対して "Hello, World!" という文字列を返します
    return "<h1>Hello, World!</h1>"


# このスクリプトが直接実行された場合に、以下のコードが実行されます
if __name__ == "__main__":
    # Flaskアプリケーションを起動します
    app.run(host="0.0.0.0", port=5000, debug=True)
