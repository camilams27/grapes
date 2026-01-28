import 'package:dio/dio.dart';
import '../../services/auth_service.dart';

/// Interceptor que injeta automaticamente o token JWT em todas as requisições
/// e trata erros de autenticação (401)
class AuthInterceptor extends Interceptor {
  final AuthService _authService = AuthService();

  @override
  Future<void> onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    // Endpoints que NÃO precisam de autenticação
    final publicEndpoints = ['/auth/login', '/auth/register'];

    final isPublicEndpoint = publicEndpoints.any(
      (endpoint) => options.path.contains(endpoint),
    );

    if (!isPublicEndpoint) {
      final token = await _authService.getToken();

      if (token != null && token.isNotEmpty) {
        options.headers['Authorization'] = 'Bearer $token';
      }
    }

    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    // Se receber 401, o token provavelmente expirou
    if (err.response?.statusCode == 401) {
      // Limpa o token salvo
      _authService.logout();

      // Aqui você poderia emitir um evento para navegar para login
      // Por enquanto, apenas passa o erro adiante
    }

    return handler.next(err);
  }

  @override
  void onResponse(Response response, ResponseInterceptorHandler handler) {
    return handler.next(response);
  }
}
