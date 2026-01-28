import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// Serviço de autenticação que gerencia o token JWT
/// Usa Flutter Secure Storage para armazenamento criptografado
class AuthService {
  static const String _tokenKey = 'grapes_jwt_token';

  final FlutterSecureStorage _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(encryptedSharedPreferences: true),
  );

  /// Salva o token JWT de forma segura
  Future<void> saveToken(String token) async {
    await _storage.write(key: _tokenKey, value: token);
  }

  /// Recupera o token JWT salvo
  Future<String?> getToken() async {
    return await _storage.read(key: _tokenKey);
  }

  /// Remove o token (logout)
  Future<void> logout() async {
    await _storage.delete(key: _tokenKey);
  }

  /// Verifica se existe um token salvo
  Future<bool> isLoggedIn() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }

  /// Limpa todos os dados salvos
  Future<void> clearAll() async {
    await _storage.deleteAll();
  }
}
