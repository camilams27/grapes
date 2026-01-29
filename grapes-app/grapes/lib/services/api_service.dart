import 'package:dio/dio.dart';
import '../core/network/dio_client.dart';
import '../models/player_model.dart';
import 'auth_service.dart';

/// Serviço de API que gerencia todas as chamadas ao backend
class ApiService {
  final Dio _dio = DioClient.http;
  final AuthService _authService = AuthService();

  // ═══════════════════════════════════════════════════════════
  // AUTENTICAÇÃO
  // ═══════════════════════════════════════════════════════════

  /// Registra um novo usuário
  /// POST /auth/register
  Future<bool> register({
    required String nickname,
    required String email,
    required String password,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/register',
        data: {'nickname': nickname, 'email': email, 'password': password},
      );

      return response.statusCode == 200 || response.statusCode == 201;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  /// Faz login e salva o token
  /// POST /auth/login
  Future<bool> login({
    required String nickname,
    required String password,
  }) async {
    try {
      final response = await _dio.post(
        '/auth/login',
        data: {'nickname': nickname, 'password': password},
      );

      if (response.statusCode == 200 && response.data != null) {
        final token = response.data['token'];
        if (token != null) {
          await _authService.saveToken(token);
          return true;
        }
      }
      return false;
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  /// Faz logout limpando o token
  Future<void> logout() async {
    await _authService.logout();
    DioClient.reset();
  }

  // ═══════════════════════════════════════════════════════════
  // PLAYER
  // ═══════════════════════════════════════════════════════════

  /// Busca dados do jogador logado
  /// GET /players/me
  Future<Player> getCurrentPlayer() async {
    try {
      final response = await _dio.get('/players/me');

      if (response.statusCode == 200 && response.data != null) {
        return Player.fromJson(response.data);
      }
      throw Exception('Falha ao buscar dados do jogador');
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  /// Busca jogador por nickname
  /// GET /players/{nickname}
  Future<Player> getPlayerByNickname(String nickname) async {
    try {
      final response = await _dio.get('/players/$nickname');

      if (response.statusCode == 200 && response.data != null) {
        return Player.fromJson(response.data);
      }
      throw Exception('Jogador não encontrado');
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  /// Adiciona XP ao jogador
  /// POST /players/{nickname}/xp
  Future<Player> addXp(String nickname, int amount) async {
    try {
      final response = await _dio.post(
        '/players/$nickname/xp',
        data: {'amount': amount},
      );

      if (response.statusCode == 200 && response.data != null) {
        return Player.fromJson(response.data);
      }
      throw Exception('Falha ao adicionar XP');
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  // ═══════════════════════════════════════════════════════════
  // HELPERS
  // ═══════════════════════════════════════════════════════════

  /// Trata erros do Dio e retorna mensagem amigável
  String _handleError(DioException e) {
    switch (e.type) {
      case DioExceptionType.connectionTimeout:
      case DioExceptionType.sendTimeout:
      case DioExceptionType.receiveTimeout:
        return 'Tempo de conexão esgotado. Verifique sua internet.';

      case DioExceptionType.connectionError:
        return 'Não foi possível conectar ao servidor.';

      case DioExceptionType.badResponse:
        final statusCode = e.response?.statusCode;
        final message = e.response?.data?['message'];

        if (statusCode == 401) {
          return 'Email ou senha incorretos.';
        } else if (statusCode == 400) {
          return message ?? 'Dados inválidos.';
        } else if (statusCode == 409) {
          return 'Este email já está cadastrado.';
        } else if (statusCode == 404) {
          return 'Recurso não encontrado.';
        }
        return message ?? 'Erro no servidor ($statusCode)';

      default:
        return 'Erro inesperado. Tente novamente.';
    }
  }
}
